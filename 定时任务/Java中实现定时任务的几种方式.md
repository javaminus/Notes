1. **Timer类和TimerTask类**： Timer类是Java SE5之前的一个定时器工具类，可用于执行定时任务。TimerTask类则表示一个可调度的任务，通常通过继承该类来实现自己的任务，然后使用Timer.schedule()方法来安排任务的执行时间。 
2. **ScheduledExecutorService类**： ScheduledExecutorService是Java SE5中新增的一个定时任务执行器，它可以比Timer更精准地执行任务，并支持多个任务并发执行。通过调用ScheduledExecutorService.schedule()或ScheduledExecutorService.scheduleAtFixedRate()方法来安排任务的执行时间。 
3. **DelayQueue**：DelayQueue是一个带有延迟时间的无界阻塞队列，它的元素必须实现Delayed接口。当从DelayQueue中取出一个元素时，如果其延迟时间还未到达，则会阻塞等待，直到延迟时间到达。因此，我们可以通过将任务封装成实现Delayed接口的元素，将其放入DelayQueue中，再使用一个线程不断地从DelayQueue中取出元素并执行任务，从而实现定时任务的调度。 

以上几种方案，相比于xxl-job这种定时任务调度框架来说，他实现起来简单，不须要依赖第三方的调度框架和类库。方案更加轻量级。  

当然这个方案也不是没有缺点的，首先，以上方案都是**基于JVM内存**的，需要把定时任务提前放进去，那如果数据量太大的话，可能会导致**OOM的问题**；另外，基于JVM内存的方案，**一旦机器重启了，里面的数据就都没有了**，所以一般都需要配合数据库的持久化一起用，并且在应用启动的时候也需要做重新加载。  

还有就是，现在很多应用都是集群部署的，那么集群中多个实例上的多个任务如何配合是一个很大的问题。 