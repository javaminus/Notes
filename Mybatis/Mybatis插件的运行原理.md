Mybatis插件的运行原理主要涉及3个关键接口：`Interceptor`、`Invocation`和`Plugin`。 

1、Interceptor：拦截器接口，定义了Mybatis插件的基本功能，包括插件的初始化、插件的拦截方法以及插件的销毁方法。 

2、Invocation：调用接口，表示Mybatis在执行SQL语句时的状态，包括SQL语句、参数、返回值等信息。 

3、Plugin：插件接口，Mybatis框架在执行SQL语句时，会将所有注册的插件封装成Plugin对象，通过Plugin对象实现对SQL语句的拦截和修改。 

插件的运行流程如下： 

1、首先，当Mybatis框架运行时，会将所有实现了Interceptor接口的插件进行初始化。 

2、初始化后，Mybatis框架会将所有插件和原始的Executor对象封装成一个InvocationChain对象。（这里使用的是责任链模式） 

3、每次执行SQL语句时，Mybatis框架都会通过InvocationChain对象依次调用所有插件的intercept方法，实现对SQL语句的拦截和修改。 

4、最后，Mybatis框架会将修改后的SQL语句交给原始的Executor对象执行，并将执行结果返回给调用方。 通过这种方式，Mybatis插件可以对SQL语句进行拦截和修改，实现各种功能，例如查询缓存、分页、分库分表等。