# Ricart-Agrawala algorithm for distributed mutual exclusion with Roucairol-Carvalho optimization
Coursework CS 6378

## Introduction
1. There are n nodes in the system, numbered from zero to n-1. Each node executes on a different machine.
2. Establish reliable socket connections (TCP) between each pair of nodes.
3. Follow this sequence until each node has successfully exited the critical section 20 times
 - Waits for a random period of time [10, 100] milliseconds before trying to enter the critical section.
 - On entry, exit it after 20 milliseconds.
4. After 20 until 40 critical section
 - Odd numbered nodes continue at the same rate as before.
 - Even numbered nodes, after exiting the critical section, wait for a random period of time [200, 500] milliseconds before trying to enter again.
 - On entry, exit it after 20 milliseconds.
5. Once a node has successfully existed the critical section 40 times, it sends a completion notification to node zero.
6. Node zero brings the entire computation to an end once its has received completion notification from all the nodes and has itself finished 40 critical section executions.

## Logging
Report the following

1. Total number of messages exchanged.
2. The maximum and minimum number of messages a node had to exchange (send requests and receive replies) to enter its critical section.
3. For each node, report the following for each of its attempts to enter the critical section
 - The number of messages exchanged.
 - The elapsed time between making a request and being able to enter the critical section.
4. A log-file for all nodes in the network, showing entering/leaving for each critical section

## Sample Config File
 # Any text following '#' should be ignored

 3 # Total number of nodes

 #NodeID - HostName - Port

 0 127.0.0.1 50000 # Location of node 0

 1 127.0.0.1 51000 # Location of node 1

 2 127.0.0.1 52000 # Location of node 2

## Usage
	$ java -jar ricartagrwala 0
	$ java -jar ricartagrwala 1
	$ java -jar ricartagrwala <n>

- Export a runnable jar file using eclipse.
- Place config.txt and ricartagrawala.jar in same folder on all the nodes.
- Execute commands on all the nodes in sequence, <n> = node number
- Thread has a time delay of 15 seconds to start all the nodes.

## License

MIT: http://vineetdhanawat.mit-license.org/