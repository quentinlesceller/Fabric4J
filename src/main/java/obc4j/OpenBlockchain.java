package obc4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;

import objects.exception.JSONException;
import objects.json.JSONArray;
import objects.json.JSONObject;
import protos.Chaincode.ConfidentialityLevel;
import protos.Openchain;
import protos.Openchain.Block;
import protos.Openchain.BlockchainInfo;
import protos.Openchain.BlockchainInfo.Builder;
import protos.Openchain.NonHashData;
import protos.Openchain.PeerEndpoint;
import protos.Openchain.PeerEndpoint.Type;
import protos.Openchain.PeerID;
import protos.Openchain.PeersMessage;
import protos.Openchain.Transaction;
import tools.URLTools;

/**
 * The Class OpenBlockchain.
 * 
 * @author Quentin Le Sceller
 */
public class OpenBlockchain implements IOpenBlockchain {

    /** The server. */
    private String server;

    /** The url tools. */
    private URLTools urlTools;

    /** The alert json not found. */
    private boolean alertJSONNotFound;

    /** The use open ssl. */
    private boolean useOpenSSL;

    /** The security enabled. */
    private boolean securityEnabled;

    /** The enroll id. */
    private String enrollID;

    /** The enroll secret. */
    private String enrollSecret;

    /**
     * Instantiates a new open blockchain.
     *
     * @param IP
     *            the ip
     * @param port
     *            the port
     */
    public OpenBlockchain(String IP, int port) {

        server = IP + ":" + port;
        urlTools = new URLTools();
        useOpenSSL = false;
        alertJSONNotFound = true;

    }

    /**
     * Instantiates a new open blockchain.
     *
     * @param IP
     *            the ip
     * @param port
     *            the port
     * @param enrollID
     *            the enroll id
     * @param enrollSecret
     *            the enroll secret
     */
    public OpenBlockchain(String IP, int port, String enrollID, String enrollSecret) {

        server = IP + ":" + port;
        urlTools = new URLTools();
        useOpenSSL = false;
        alertJSONNotFound = true;
        securityEnabled = true;
        this.enrollID = enrollID;
        this.enrollSecret = enrollSecret;

    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBLockchain#getBlock(int)
     */
    @Override
    public Block getBlock(int blockNumber) {
        String request = "/chain/blocks/" + blockNumber;
        URL url = createURLRequest(request);

        JSONObject blockJSON = null;

        if (useOpenSSL) {
            blockJSON = urlTools.getHTTPSJSON(url);
        } else {
            blockJSON = urlTools.getJSON(url);
        }

        protos.Openchain.Block.Builder blockBuilder = Openchain.Block.newBuilder();

        try {
            Integer version = blockJSON.getInt("version");
            blockBuilder.setVersion(version);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();

        }

        try {
            ByteString consensusMetadata = ByteString.copyFromUtf8(blockJSON.getString("consensusMetadata"));
            blockBuilder.setConsensusMetadata(consensusMetadata);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        // Non hash data not full.
        try {
            JSONObject nonHashDataJSON = blockJSON.getJSONObject("nonHashData");
            try {
                JSONObject timestampJSON = nonHashDataJSON.getJSONObject("localLedgerCommitTimestamp");
                NonHashData nonHashData = NonHashData.newBuilder()
                        .setLocalLedgerCommitTimestamp(timestampBuilder(timestampJSON)).build();
                blockBuilder.setNonHashData(nonHashData);
            } catch (JSONException e) {
                if (alertJSONNotFound)
                    e.printStackTrace();
            }

        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString previousBlockHash = ByteString.copyFromUtf8(blockJSON.getString("previousBlockHash"));
            blockBuilder.setPreviousBlockHash(previousBlockHash);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString stateHash = ByteString.copyFromUtf8(blockJSON.getString("stateHash"));
            blockBuilder.setStateHash(stateHash);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        blockBuilder.setTimestamp(timestampBuilder(blockJSON));

        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        try {
            JSONArray transactionsJSONArray = blockJSON.getJSONArray("transactions");
            for (int i = 0; i < transactionsJSONArray.length(); i++) {
                transactions.add(txBuilder(transactionsJSONArray.getJSONObject(i)));
            }
            Iterable<Transaction> iteratorTransactions = transactions;
            blockBuilder.addAllTransactions(iteratorTransactions);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        return blockBuilder.build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBLockchain#getBlockchain()
     */
    @Override
    public BlockchainInfo getBlockchainInfo() {
        String request = "/chain/";
        URL url = createURLRequest(request);

        JSONObject chainJSON = null;

        if (useOpenSSL) {
            chainJSON = urlTools.getHTTPSJSON(url);
        } else {
            chainJSON = urlTools.getJSON(url);
        }

        Builder blockchainInfoBuilder = BlockchainInfo.newBuilder();

        try {
            Long height = chainJSON.getLong("height");
            blockchainInfoBuilder.setHeight(height);

        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString currentBlockHash = ByteString.copyFromUtf8(chainJSON.getString("currentBlockHash"));
            blockchainInfoBuilder.setCurrentBlockHash(currentBlockHash);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString previousBlockHash = ByteString.copyFromUtf8(chainJSON.getString("previousBlockHash"));
            blockchainInfoBuilder.setPreviousBlockHash(previousBlockHash);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        return blockchainInfoBuilder.build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBLockchain#deploy()
     */
    @Override
    public JSONObject deploy(String type, String path, String function, String[] args) {
        String request = "/devops/deploy";
        URL url = createURLRequest(request);

        JSONObject response = null;
        try {
            JSONObject bodyJSON = new JSONObject();
            bodyJSON.put("type", type);

            bodyJSON.put("chaincodeID", new JSONObject().put("path", path));

            JSONObject ctorMSg = new JSONObject();
            ctorMSg.put("function", function);

            JSONArray argsArrayJSON = new JSONArray();

            for (String arg : args) {
                argsArrayJSON.put(arg);
            }

            ctorMSg.put("args", argsArrayJSON);
            bodyJSON.put("ctorMsg", ctorMSg);

            if (securityEnabled) {
                bodyJSON.put("secureContext", enrollID);
            }

            if (useOpenSSL) {
                response = urlTools.sendHTTPSPost(url, bodyJSON.toString());
            } else {
                response = urlTools.sendPost(url, bodyJSON.toString());
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return response;

    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBLockchain#invoke()
     */
    @Override
    public JSONObject invoke(String type, String name, String function, String[] args) {
        String request = "/devops/invoke";
        URL url = createURLRequest(request);

        JSONObject response = null;
        try {
            JSONObject chaincodeSpecJSON = new JSONObject();
            chaincodeSpecJSON.put("type", type);

            chaincodeSpecJSON.put("chaincodeID", new JSONObject().put("name", name));

            JSONObject ctorMSg = new JSONObject();
            ctorMSg.put("function", function);

            JSONArray argsArrayJSON = new JSONArray();

            for (String arg : args) {
                argsArrayJSON.put(arg);
            }

            ctorMSg.put("args", argsArrayJSON);
            chaincodeSpecJSON.put("ctorMsg", ctorMSg);

            if (securityEnabled) {
                chaincodeSpecJSON.put("secureContext", enrollID);
            }

            JSONObject bodyJSON = new JSONObject().put("chaincodeSpec", chaincodeSpecJSON);

            if (useOpenSSL) {
                response = urlTools.sendHTTPSPost(url, bodyJSON.toString());
            } else {
                response = urlTools.sendPost(url, bodyJSON.toString());
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return response;
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBLockchain#query()
     */
    @Override
    public JSONObject query(String type, String name, String function, String[] args) {
        String request = "/devops/query";
        URL url = createURLRequest(request);

        JSONObject response = null;
        try {
            JSONObject chaincodeSpecJSON = new JSONObject();
            chaincodeSpecJSON.put("type", type);

            chaincodeSpecJSON.put("chaincodeID", new JSONObject().put("name", name));

            JSONObject ctorMSg = new JSONObject();
            ctorMSg.put("function", function);

            JSONArray argsArrayJSON = new JSONArray();

            for (String arg : args) {
                argsArrayJSON.put(arg);
            }

            ctorMSg.put("args", argsArrayJSON);
            chaincodeSpecJSON.put("ctorMsg", ctorMSg);

            if (securityEnabled) {
                chaincodeSpecJSON.put("secureContext", enrollID);
            }

            JSONObject bodyJSON = new JSONObject().put("chaincodeSpec", chaincodeSpecJSON);

            if (useOpenSSL) {
                response = urlTools.sendHTTPSPost(url, bodyJSON.toString());
            } else {
                response = urlTools.sendPost(url, bodyJSON.toString());
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return response;

    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBLockchain#getPeers()
     */
    @Override
    public PeersMessage getPeers() {
        String request = "/network/peers/";
        URL url = createURLRequest(request);

        JSONObject peersJSON = null;

        if (useOpenSSL) {
            peersJSON = urlTools.getHTTPSJSON(url);
        } else {
            peersJSON = urlTools.getJSON(url);
        }

        protos.Openchain.PeersMessage.Builder peersMessageBuilder = PeersMessage.newBuilder();

        ArrayList<PeerEndpoint> peers = new ArrayList<PeerEndpoint>();
        try {
            JSONArray peersJSONArray = peersJSON.getJSONArray("transactions");
            for (int i = 0; i < peersJSONArray.length(); i++) {
                peers.add(peerBuilder(peersJSONArray.getJSONObject(i)));
            }
            Iterable<PeerEndpoint> iteratorPeers = peers;
            peersMessageBuilder.addAllPeers(iteratorPeers);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }
        // TODO Not finished
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBLockchain#getTransaction(int)
     */
    @Override
    public Transaction getTransaction(String uuid) {
        String request = "/transactions/" + uuid;
        URL url = createURLRequest(request);
        JSONObject transactionJSON = null;

        transactionJSON = urlTools.getJSON(url);

        return txBuilder(transactionJSON);
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBlockchain#disableJSONNotFoundAlert()
     */
    @Override
    public void disableJSONNotFoundAlert() {
        alertJSONNotFound = false;

    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBlockchain#enableOpenSSL()
     */
    @Override
    public void enableOpenSSL() {
        useOpenSSL = true;

    }

    /**
     * Creates the url request.
     *
     * @param request
     *            the request
     * @return the url
     */
    private URL createURLRequest(String request) {
        URL url = null;
        
        if (useOpenSSL){
            try {
                url = new URL("http://" + server + request);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                url = new URL("https://" + server + request);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
       

        return url;
    }

    /**
     * Tx builder.
     *
     * @param transactionJSON
     *            the transaction json
     * @return the transaction
     */
    private Transaction txBuilder(JSONObject transactionJSON) {
        protos.Openchain.Transaction.Builder transactionBuilder = Transaction.newBuilder();

        try {
            Integer typeJSON = transactionJSON.getInt("type");
            switch (typeJSON) {

            case 0:
                transactionBuilder.setType(Transaction.Type.UNDEFINED);
                break;

            case 1:
                transactionBuilder.setType(Transaction.Type.CHAINCODE_NEW);
                break;
            case 2:
                transactionBuilder.setType(Transaction.Type.CHAINCODE_UPDATE);
                break;
            case 3:
                transactionBuilder.setType(Transaction.Type.CHAINCODE_EXECUTE);
                break;
            case 4:
                transactionBuilder.setType(Transaction.Type.CHAINCODE_QUERY);
                break;
            case 5:
                transactionBuilder.setType(Transaction.Type.CHAINCODE_TERMINATE);
                break;
            default:
                System.out.println("No Transaction Type found.");
                break;

            }
        } catch (JSONException e) {

            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString chaincodeID = ByteString.copyFromUtf8(transactionJSON.getString("chaincodeID"));
            transactionBuilder.setChaincodeID(chaincodeID);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString payload = ByteString.copyFromUtf8(transactionJSON.getString("payload"));
            transactionBuilder.setPayload(payload);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString metadata = ByteString.copyFromUtf8(transactionJSON.getString("metadata"));
            transactionBuilder.setMetadata(metadata);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            String uuid = transactionJSON.getString("uuid");
            transactionBuilder.setUuid(uuid);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            JSONObject timestampJSON = transactionJSON.getJSONObject("timestamp");
            transactionBuilder.setTimestamp(timestampBuilder(timestampJSON));

        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            String confidentialityJSON = transactionJSON.getString("confidentialityLevel");
            switch (confidentialityJSON) {
            case "PUBLIC":
                transactionBuilder.setConfidentialityLevel(ConfidentialityLevel.PUBLIC);
                break;

            case "CONFIDENTIAL":
                transactionBuilder.setConfidentialityLevel(ConfidentialityLevel.CONFIDENTIAL);
                break;
            default:
                System.out.println("Confidentiality Level not found.");
                break;
            }
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString nonce = ByteString.copyFromUtf8(transactionJSON.getString("nonce"));
            transactionBuilder.setNonce(nonce);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString cert = ByteString.copyFromUtf8(transactionJSON.getString("cert"));
            transactionBuilder.setCert(cert);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString signature = ByteString.copyFromUtf8(transactionJSON.getString("signature"));
            transactionBuilder.setSignature(signature);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        return transactionBuilder.build();
    }

    /**
     * Timestamp builder.
     *
     * @param timestampJSON
     *            the timestamp json
     * @return the timestamp
     */
    private Timestamp timestampBuilder(JSONObject timestampJSON) {
        com.google.protobuf.Timestamp.Builder timestampBuilder = Timestamp.newBuilder();

        try {
            timestampBuilder.setSeconds(timestampJSON.getLong("seconds"));
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }
        try {
            timestampBuilder.setNanos(timestampJSON.getInt("nanos"));
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        return timestampBuilder.build();
    }

    /**
     * Peer builder.
     *
     * @param peerJSON
     *            the peer json
     * @return the peer endpoint
     */
    private PeerEndpoint peerBuilder(JSONObject peerJSON) {
        protos.Openchain.PeerEndpoint.Builder peerBuilder = PeerEndpoint.newBuilder();

        try {
            JSONObject peerIDJSON = peerJSON.getJSONObject("peerID");
            PeerID peerID = PeerID.newBuilder().setName(peerIDJSON.getString("name")).build();
            peerBuilder.setID(peerID);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            String address = peerJSON.getString("PeerID");
            peerBuilder.setAddress(address);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            String type = peerJSON.getString("Type");
            switch (type) {
            case "UNDEFINED":
                peerBuilder.setType(Type.UNDEFINED);
                break;

            case "VALIDATOR":
                peerBuilder.setType(Type.VALIDATOR);
                break;

            case "NON_VALIDATOR":
                peerBuilder.setType(Type.NON_VALIDATOR);
                break;
            default:
                break;
            }
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        try {
            ByteString pkiID = ByteString.copyFromUtf8(peerJSON.getString("pkiID"));
            peerBuilder.setPkiID(pkiID);
        } catch (JSONException e) {
            if (alertJSONNotFound)
                e.printStackTrace();
        }

        return peerBuilder.build();
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBlockchain#registrarUser()
     */
    @Override
    public Boolean registrarUser() {
        Boolean success = null;
        if (securityEnabled) {
            success = false;
            String request = "/registrar";
            URL url = createURLRequest(request);

            JSONObject response = null;
            try {
                JSONObject bodyJSON = new JSONObject();
                bodyJSON.put("enrollID", enrollID);
                bodyJSON.put("enrollSecret", enrollSecret);

                if (useOpenSSL) {
                    response = urlTools.sendHTTPSPost(url, bodyJSON.toString());
                } else {
                    response = urlTools.sendPost(url, bodyJSON.toString());
                }
                if (response.getString("OK").contains("Login successful")) {
                    success = true;
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

        }
        return success;
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBlockchain#deleteUser(java.lang.String)
     */
    @Override
    public Boolean deleteUser(String enrollmentID) {
        Boolean success = null;
        if (securityEnabled) {
            success = false;
            String request = "/registrar/" + enrollmentID;
            URL url = createURLRequest(request);

            JSONObject response = null;
            try {

                if (useOpenSSL) {
                    response = urlTools.sendHTTPSDelete(url);
                } else {
                    response = urlTools.sendDelete(url);
                }
                if (response.getString("OK").contains("Deleted login token and directory for user")) {
                    success = true;
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

        }
        return success;
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBlockchain#getRegistrar(java.lang.String)
     */
    @Override
    public Boolean getRegistrar(String enrollmentID) {
        Boolean success = null;
        if (securityEnabled) {
            success = false;
            String request = "/registrar/" + enrollmentID;
            URL url = createURLRequest(request);

            JSONObject registrarJSON = null;

           
                if (useOpenSSL) {
                    registrarJSON = urlTools.getHTTPSJSON(url);
                } else {
                    registrarJSON = urlTools.getJSON(url);
                }

                try {
                    if (registrarJSON.getString("OK").contains("is already logged in.")) {
                        success = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

          

        }
        return success;
    }

    /*
     * (non-Javadoc)
     * 
     * @see obc4j.IOpenBlockchain#getEnrollmentCertificate(java.lang.String)
     */
    @Override
    public String getEnrollmentCertificate(String enrollmentID) {
        String response = null;
        if (securityEnabled) {

            String request = "/registrar/" + enrollmentID + "/ecert";
            URL url = createURLRequest(request);

            JSONObject registrarJSON = null;

          
                if (useOpenSSL) {
                    registrarJSON = urlTools.getHTTPSJSON(url);
                } else {
                    registrarJSON = urlTools.getJSON(url);
                }

                try {
                    response = registrarJSON.getString("OK");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

          

        }
        return response;

    }

}