# Infrastructure

The setup is a copied from the integration tests of [hyperledger fabric java SDK]( https://github.com/hyperledger/fabric-sdk-java).
It consists of two organisations, represented by two certificate authorities (CA), 
each managing two peers (peer0.org1, peer1.org1, peer0.org2, peer1.org2).
They all share a single orderer.

All the certificates are pre generated and and every container can access them. 
Each CA starts up with an administrative super user (admin:adminpw).

The infrastructure does not know anything about channels - there is no channel configuration in this module.