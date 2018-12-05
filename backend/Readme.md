# Backend

The backend installs the chaincode on the peers and does the communcation between client software and the distributed ledger. 

## Chaincode

The chaincode is what we usually would call a smart contract. It is the piece of software, 
which runns in a distributed manner (on each peer) and has access to the distributed ledger.
The chaincode must produce, in any case, deterministic results and should be kept as small as possible.

The chaincode itself is a sub module of this module located in the folder *chaincode/sample1*.
It consists of a single Java class *VehicleTracking.java*. 

## Manage Chaincode with SDK
In order to communicate with the chaincode (or the ledger), the following is needed

- create users with appropriate rights
- create a channel (between multiple organisations)
- install the chaincode on the created channel
- instantiate the chaincode

This is done only once, and than the channel is serialized to the file system *foo.ser*.
If that file is available, setting up the users, chaincode and the channel is ommitted.

## Provide a Rest API for Communication with the Chaincode

The Rest API and the Chaincode API provide the same functionality

- insert TrachingRecord
- getActualList - returns a list with the most recent TrackingRecord for each vehicle
- getHistory - returns a list of all TrackingRecords of a singel vehicle

## Users and Organisations

The rest endpoint provides access to two users, which are mapped to the two organisations in the hyperledger fabric setup as follows

- PlanetExpress:secret -> org1.example.com
- LogicticAG:secret -> org2.example.com


# Known Issues:

- If *java.lang.UnsupportedOperationException: Reflective setAccessible(true) disabled* occures, 
maybee your IDE or environment sets *io.netty.tryReflectionSetAccessible=false*, 
this system property must be empty or *true*.
