1、首先将去检查这个指令的参数是否能在**常量池**中定位到这个类的符号引用，并且检查这个符号引用代表的类是否已被加载过、解析和初始化过。如果没有，那必须先执行相应的类加载过程

2、分配内存。JVM会在堆中为对象分配内存空间（无JIT优化情况下）。在HotSpot中，对象的内存分配有两种方式，分别是指针碰撞和空闲列表法。 

​	○指针碰撞：当堆中的内存是连续的，JVM使用一个指针来标记当前可用的内存位置，然后将指针向前移动分配对象所需的内存大小。 

​	○空闲列表：当堆中的内存是离散的，JVM会维护一个空闲列表，记录可用的内存块。在分配对象时，JVM会遍历空闲列表，找到足够大小的内存块进行分配。 （分配内存解决并发有两种手段，一个是CAS+失败重试，一个是Thread Local Allocation  Buffer（TLAB）) 

3、内存分配完成后，虚拟机需要将分配到的内存空间都初始化为零值，这一步确保了对象的字段在创建时都有默认值。如int被初始化为0，引用类型被初始化为null 

4、设置对象头。 该实例所对应的类、如何才能找到类的元数据信息、对象的哈希码、对象的 GC 分代年龄，轻量级锁等等信息 

5、调用该类的构造方法，初始化对象。如按照程序员意愿进行赋值 

6、返回对象引用，当对象完成创建之后，返回一个该对象的引用，后续Java程序就可以使用这个引用来操作对象了。 