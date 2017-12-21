# REST

Program description: 

The procedure is intended to obtain a warehouse on git, and then obtain the code of each submission of the warehouse to calculate the complexity of the submission code. The calculation of complexity is through multiple workers. The number of workers can be imported from the console applying REST architecture, and then the manager summarizes the results of each workerStatistics, the calculation of each worker, the calculation time, and the calculation of the complexity of each submission.


Procedure steps:

1. Enter the program through the main method main entrance, the first thing to enter is to calculate the number of worker and you want to calculate the warehouse address, for example: https://github.com/ZJDong/SocketServer/

2. The manager obtains the sha values for each submission of the sub-repository through the git method and gets the code for each submission

3. The manager will distribute the collection of code submitted by the warehouse code and distribute it to each worker at the same time

4. The worker calculates the tasks that he obtains, calculates the complexity of each submission, and calculates the time and returns to manager

5. The manager summarizes the work results of each worker


Implementation principle: 

Create multiple REST communication threads by entering the number of worer, each thread is a worker, and these threads can work simultaneously without affecting each other. The call to thread uses the REST mode to call, pass the parameters in, calculate, and return the results.


To test: please import Spring Boot(https://projects.spring.io/spring-boot/#quick-start) frame before compiling, then start the progress and follow the operation instruction.

Name: Zhijian Dong


Student Number: 17313074
