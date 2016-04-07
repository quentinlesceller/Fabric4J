# Fabric4J

Fabric4J is a simple Java API for [Fabric](https://github.com/hyperledger/fabric).

## Installation

Add this project as maven dependency or simply import the jar located in the jar folder in your project.

## Usage

Fabric4J is extremely simple to use, just start by declaring it:

`Blockchain blockchain = new Blockchain(server,port);`

Or if security is enabled:

`Blockchain blockchain = new Blockchain(server,port, enrollID, enrollSecret)`

Then you can start using it ! For example:

`BlockchainInfo info = blockchain.getBlockchainInfo();`

`System.out.println(info.getHeight());`

All the methods are explained in the [Javadoc](doc/).

## Credits

Thank to all Hyperledger developpers.

## License

This software is made available under the [Apache License Version 2.0](LICENSE).
