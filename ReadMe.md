# Two Phase Commit Protocol

Two-phase commit protocol (or 2PC) is a mechanism for implementing a transaction across different software components

One of the important participants in a distributed transaction is the transaction coordinator. The distributed transaction consists of two steps:

1) Prepare phase — during this phase, all participants of the transaction prepare for commit and notify the coordinator that they are ready to complete the transaction
2) Commit or Rollback phase — during this phase, either a commit or a rollback command is issued by the transaction coordinator to all participants


In this Project there are 3 services:
1) Order Service : Co-ordinator
2) Account Service : Participant
3) Product Service : Participant


## Working
- The Order Service acts as a co-ordinator.
- It recieves the order which includes customer Name, Product , Quantity of the product.
- It then initiates Prepare Phase.
- In the Prepare Phase all the participants checks the availability like sufficient product quantity and sufficient account balance.
- Participants then holds on to the resource by the transaction Id and sends True to the co-ordinator.
- Incase the participant is not able to hold on to the resources due to insufficient quantity or balance it sends False to the co-ordinator.
- If the co-oridinator recieves True from all the participants, it then initiates the commit phase.
- In case any of the participants respionds by False, it initiates the Rollback phase.
- In case of rollback, all the resources hold in the transaction are released back.

## Data Store
- All the data is stored in-memory using HashMap for simplicity.
- All product Quantity are initialised to 100
- Account balance is initialised to 1000.
- Product Prices are:
    - apple=20
    - orange=35
    - banana=10
- Any Database can be used in the similar fashion.

## Steps:
- Maven Build all the 3 services.
- Run Maven Build of Order Service with -DskipTests=true or start Product Service and Account Service before running the maven build of Order Service.
- Start all the 3 services:
- Open Brower and create order

http://localhost:8084/createOrder?accountName=jainesh&product=orange&quantity=10

- Check logs:
Order Service Logs
```
Recieved Create Order :
Product :orange
Quantity :10
accountName : jainesh
Fetching Price of Product
Price of orange: 35
Phase 1 : Prepare
Phase 2 : Commit
Transaction Completed Successfully
```

Account Service Logs:
```
Transation :{}
Bank :{tanmay=1000, jainesh=650, siya=1000}
```

Product Service Logs:
```
Transation :{}
Inventory :{orange=90, banana=100, apple=100}
```