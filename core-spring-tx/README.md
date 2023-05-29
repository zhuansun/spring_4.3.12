
# spring基于注解的事务和源码分析


突然发现本文的图都裂了！各位可以移步跳转到我的个人博客或者CSDN上查看
- [spring基于注解的事务和源码分析-hexo](https://pusu.info/2019/07/09/spring%E6%B3%A8%E8%A7%A3%E4%BA%8B%E5%8A%A1%E5%BC%80%E5%8F%91%E5%92%8C%E6%BA%90%E7%A0%81%E8%A7%A3%E8%AF%BB/#more)
- [spring基于注解的事务和源码分析-csdn](https://blog.csdn.net/qq_24821203/article/details/95320955)



spring总体来说，有几大模块
- IOC
- AOP
- 事务

## 一、使用

spirng的事务，使用有两种方式：一种是基于XML，一种是基于注解配置。 在当前springboot较火的时代，基于注解的配置更容易深得人心。所以本文就使用注解的方式来实现spring的事务控制。


### 基于注解的使用

#### 1.导入事务所必需的最小maven依赖

- 在平时的开发中，一定要注意对依赖的管理，每一个依赖有什么作用，不要随便导入，最后造成依赖冲突，很难管理。
- 在spring事务的开发中，必须要导入的依赖很少，只有三个而已

```
<dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.3.12.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>4.3.12.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.16</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```


![spirng事务的依赖关系](http://youdao-note-images.oss-cn-hangzhou.aliyuncs.com/2019-07/7e1e7-WechatIMG17.png)


#### 2.准备数据库和数据表以及配置数据库

- 事务都是和数据库有关系的，所以我们要准备数据库。这里对于测试推荐一个网站，免费提供测试数据库：https://www.db4free.net/
- 我创建的测试数据库是：
	 - url: jdbc:mysql://db4free.net/core_tx_db
     - username: core_tx_user
     - password: 87654321
- 使用该网站提供的phpMyAdmin进行登陆: https://www.db4free.net/phpMyAdmin/
- 如果需要自己通过数据库管理工具进行登录的话，请确保你的数据库驱动是最新的！否则可能无法连接。

![数据库配置](http://youdao-note-images.oss-cn-hangzhou.aliyuncs.com/2019-07/5e0b3-WechatIMG18.png)


#### 4.实现配置类，并配置数据源

- 对于任何一个配置类，都是需要@Configuration进行标注的
- 上一步，我们创建了数据库，现在在这里配置一下数据源和操作数据的JdbcTemplate

```
@Configuration
public class MainConfig {

    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://db4free.net/core_tx_db");
        dataSource.setUsername("core_tx_user");
        dataSource.setPassword("87654321");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws PropertyVetoException {
        return new JdbcTemplate(dataSource());
    }

}

```

- 只有数据源配置好了，才能继续之后的操作。


#### 3.实现业务逻辑类

- 对于一个正常的开发框架，都是MVC三层设计，这里就简单的模拟一下这种设计模式
- dao层，需要引入数据源，并简单的实现了一个插入的方法
```
@Repository
public class PersonDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void insert(){
        String sql = "INSERT INTO `person`(name,age) VALUES(?,?)";
        String username = UUID.randomUUID().toString().substring(0, 5);
        jdbcTemplate.update(sql, username,19);
    }

}

```
- service层：需要引入dao层，并实现了插入的方法
```
@Service
public class PersonService {

    @Autowired
    PersonDao personDao;

    public void insert(){
        personDao.insert();
        System.out.println("插入完成...");
    }

}

```

#### 4.在配置类中进行剩下的配置和事务的配置

- 剩下的配置就是开启spirng的自动扫描，因为我们的dao层和service层添加了@Repository和@Service注解，需要开启自动扫描，才能纳入Spring容器中
```
@Configuration
@ComponentScan("com.zspc.core.spring.tx")
public class MainConfig {
	//...
}
```
- 之后，就是事务的配置，事务配置分为了两个部分：
	- 一是开启spring的事务管理，让我们的当前项目具备事务的能力。
	- 二是标注需要启动的事务的方法。
	- 我们分别来看这两个步骤

- 一是开启spring的事务管理，让我们的当前项目具备事务的能力。
	- 开启spring的事务管理，分为两步
	- 第一步：将spirng的事务管理器纳入spirng容器中
	```
	@Bean
    public PlatformTransactionManager transactionManager() throws PropertyVetoException {
        return new DataSourceTransactionManager(dataSource());
    }
	```
	- 第二步：开启spring的事务
	```
	@EnableTransactionManagement
	```
	- 这样第一大步就ok了。下面进行第二大步

- 二是标注需要启动的事务的方法。
	- 在我们的service层，对insert方法添加上事务，表明该方法需要事务。
	```
	@Transactional(rollbackFor = Exception.class)
    public void insert(){
        personDao.insert();
        System.out.println("插入完成...");
    }
	```
- 至此，全部的配置，都可以了。下面附上最终的配置类,全部的代码，可以在我的github进行查看
```
@Configuration
@ComponentScan("com.zspc.core.spring.tx")
@EnableTransactionManagement
public class MainConfig {


    @Bean
    public DataSource dataSource() throws PropertyVetoException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:mysql://db4free.net/core_tx_db");
        dataSource.setUsername("core_tx_user");
        dataSource.setPassword("87654321");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws PropertyVetoException {
        return new JdbcTemplate(dataSource());
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws PropertyVetoException {
        return new DataSourceTransactionManager(dataSource());
    }

}

```


#### 编写测试类，进行测试

```
public class TXTest {
    @Test
    public void test(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
        PersonService personService = applicationContext.getBean(PersonService.class);
        personService.insert();
        applicationContext.close();
    }
}

```

- 测试的时候，只需要测试正常情况，和异常情况，正常情况就不说了，异常情况就是：在service中模拟抛出一个异常： int a = 10/0; 查看数据库中是否会有记录。
- 下面是我的测试结果：

![测试结果](http://youdao-note-images.oss-cn-hangzhou.aliyuncs.com/2019-07/18bdc-WechatIMG21.png)







### 基于XML的使用

#### 略




## 二、源码分析


在分析之前，我们先看一下事务都是怎么用的，详细的使用方法可以参考上面的步骤，大致的步骤可以分为三步：
- 在配置类中开启事务
```
@EnableTransactionManagement
public class MainConfig {...}
```
- 在容器中注入事务的管理器
```
@Bean
public PlatformTransactionManager transactionManager() throws PropertyVetoException {
    return new DataSourceTransactionManager(dataSource());
}
```
- 需要开启事务的方法上开启事务
```
@Transactional(rollbackFor = Exception.class)
```

所以，要分析源码，我们就需要一点一点的看看这三步骤分别作了什么？


### @EnableTransactionManagement的作用是什么？

- 进去看这个注解，我们发现这个注解上面有一个@Import，说明凡是使用了这个注解的地方，都会朝我们的容器中导入组件。（和之前aop使用的@EnableAspectJAutoProxy是一样的）
- 这里要注意，@EnableTransactionManagement 上的@Import导入的是一个 Selector，含义就是：Selecotr会返回需要导入的组件的全类名数组。spring会将这个数组所包含的组件全都导入spring中。
- 具体怎么导入的，我们这里不关注，我们只关注在事务开发中，具体给我们导入了哪些组件呢？
- 我们进入这个Selector，（TransactionManagementConfigurationSelector），通过源码我们很容易的看懂，它会根据不同的mode，返回不同的数组。

```
@Override
protected String[] selectImports(AdviceMode adviceMode) {
	switch (adviceMode) {
		case PROXY:
			return new String[] {AutoProxyRegistrar.class.getName(), ProxyTransactionManagementConfiguration.class.getName()};
		case ASPECTJ:
			return new String[] {TransactionManagementConfigUtils.TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME};
		default:
			return null;
	}
}
```

- 那么，我们事务开发中，传入进来的mode是什么呢？ 这个就需要到selector的上一层，也就是我们的 @EnableTransactionManagement这个类里面来，我们发现它里面定一个一个mode()，默认就是 AdviceMode.PROXY.
- 所以，回到selector，我们这个@Import(TransactionManagementConfigurationSelector.class)返回的全类名数组就包含了下面两个组件：
	- AutoProxyRegistrar.class.getName()
	- ProxyTransactionManagementConfiguration.class.getName()
- 下一步就是分析这两个组件是干什么用的？和我们的事务有什么关系呢？


#### 注入的组件一：AutoProxyRegistrar的作用是什么？

- 进入这个类，这个类只有一个方法，就是registerBeanDefinitions，表示注册bean定义。他的核心方法是下面这个：
- AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry); 表示朝我们的容器中注册一个xxxAutoProxyCreator,最后注册的是：InfrastructureAdvisorAutoProxyCreator.class
- 在这里说一个题外话：我们注入xxxAutoProxyCreator的方法所在的类是：AopConfigUtils，注意这个类，在我们的aop开发中， @EnableAspectJAutoProxy 进行注入的时候，也是通过这个类的。可以自己看一看。
- 所以说：AutoProxyRegistrar的作用，就是朝我们的容器中注入一个InfrastructureAdvisorAutoProxyCreator，注意这个xxxAutoProxyCreator的继承关系，他是一个SmartInstantiationAwareBeanPostProcessor
- 既然是SmartInstantiationAwareBeanPostProcessor，这就表示和我们之前的aop的逻辑是一样的，这部分稍后再说。
- 总结：AutoProxyRegistrar的作用是给容器中导入一个InfrastructureAdvisorAutoProxyCreator，这个InfrastructureAdvisorAutoProxyCreator是一个SmartInstantiationAwareBeanPostProcessor。


#### 注入的组件二：ProxyTransactionManagementConfiguration 的作用是什么？

- ProxyTransactionManagementConfiguration 是一个配置类，注意它头上的@Configuration，既然是一个配置类，我们来看这个配置类做了什么
	- 在容器中注入事务增强器：BeanFactoryTransactionAttributeSourceAdvisor
	- 在容器中注入事务拦截器：TransactionInterceptor
	- 另外，我们看到还在容器中注入了一个事务的属性信息：TransactionAttributeSource,这个的作用主要是给上面两个服务的。
- 注入的事务增强器，事务拦截器，和事务的属性信息三者之前得关系如下图：

![spring-tx-增强器拦截器属性信息三者的关系](http://youdao-note-images.oss-cn-hangzhou.aliyuncs.com/2019-07/cbd02-spring-tx-增强器拦截器属性信息三者的关系.png)

- 然后我们来具体的分析这三个都有什么作用。

##### 事务的属性信息:TransactionAttributeSource的作用

他的作用就是没啥作用，主要是为了 事务的增强器和事务的拦截器 进行服务的，主要是保存了一些事务的信息。

但是在事务的属性信息里面，有一个重要的点，就是事务的属性信息里面，包含了事务的解析器：SpringTransactionAnnotationParser

这个解析器，主要是用来解析我们的事务注解（@Transactional）的。 解析的主要方法如下：

```
protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
	RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();
	Propagation propagation = attributes.getEnum("propagation");
	rbta.setPropagationBehavior(propagation.value());
	Isolation isolation = attributes.getEnum("isolation");
	rbta.setIsolationLevel(isolation.value());
	rbta.setTimeout(attributes.getNumber("timeout").intValue());
	rbta.setReadOnly(attributes.getBoolean("readOnly"));
	rbta.setQualifier(attributes.getString("value"));
	ArrayList<RollbackRuleAttribute> rollBackRules = new ArrayList<RollbackRuleAttribute>();
	Class<?>[] rbf = attributes.getClassArray("rollbackFor");
	for (Class<?> rbRule : rbf) {
		RollbackRuleAttribute rule = new RollbackRuleAttribute(rbRule);
		rollBackRules.add(rule);
	}
	String[] rbfc = attributes.getStringArray("rollbackForClassName");
	for (String rbRule : rbfc) {
		RollbackRuleAttribute rule = new RollbackRuleAttribute(rbRule);
		rollBackRules.add(rule);
	}
	Class<?>[] nrbf = attributes.getClassArray("noRollbackFor");
	for (Class<?> rbRule : nrbf) {
		NoRollbackRuleAttribute rule = new NoRollbackRuleAttribute(rbRule);
		rollBackRules.add(rule);
	}
	String[] nrbfc = attributes.getStringArray("noRollbackForClassName");
	for (String rbRule : nrbfc) {
		NoRollbackRuleAttribute rule = new NoRollbackRuleAttribute(rbRule);
		rollBackRules.add(rule);
	}
	rbta.getRollbackRules().addAll(rollBackRules);
	return rbta;
}

```


##### 事务的增强器：BeanFactoryTransactionAttributeSourceAdvisor

- 之前说了，事务的增强器包含了事务的属性信息。至于事务的属性信息是什么， 上面已经说过了。
- 然后我们的事务增强器还包含了一个事务的拦截器。事务拦截器只一个非常重要的内容。我们在下面说。
- 事务的增强器被注入到了spirng容器中。那么这个增强器什么时候被用的呢？——他是在spring创建bean（这里的bean是指我们自定义的bean）的时候，开始执行，扫描被Transactional注释的类的方法，并提供TransactionInterceptor，来代理被注释的方法。


##### 事务的拦截器：TransactionInterceptor
- 这个类是我们的作用核心类。它的作用就是在我们的目标方法执行之前，进行拦截，从而实现事务的功能。
- 我们先来分析一下这个类，通过继承关系看到，这个类是一个MethodInterceptor
```
public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {...}
```
- 联想到我们之前看到的aop源码，spring实现aop的功能，简单的总结就是：封装一个代理对象，在调用目标方法之前，调用这些methodInterceptor，然后就实现了aop的功能。
- 所以，针对我们的transaction，原理都是一样的： 在调用目标方法之前，先调用这个TransactionInterceptor，然后就可以实现我们的事务了。
- 具体是怎么实现的呢？我们来一点一点的看。这里所说的实现，是指我们的代理对象创建好之后的调用逻辑，也就是我们目标方法执行时，是怎么实现事务的。

##### 事务执行的具体步骤

- 是调用到TransactionInterceptor的invoke方法：代理对象要执行目标方法了，方法拦截器就开始工作，invoke就表示方法拦截器的执行
- invoke()方法里面调用了 invokeWithinTransaction()这个方法
- invokeWithinTransaction()这个方法，这个方法就是事务的核心方法了。代码不多，我们直接看源码。

```
/**
 * General delegate for around-advice-based subclasses, delegating to several other template
 * methods on this class. Able to handle {@link CallbackPreferringPlatformTransactionManager}
 * as well as regular {@link PlatformTransactionManager} implementations.
 * @param method 需要执行的方法名，对应我这个项目的话就是：public void com.zspc.core.spring.tx.service.PersonService.insert()
 * @param targetClass 需要执行目标方法的具体类，对应我这个项目的话就是：class com.zspc.core.spring.tx.service.PersonService
 * @param invocation 回调方法
 * @return 目标方法的返回值，如果有的话
 * @throws Throwable 目标方法执行时可能抛出的异常
 */
protected Object invokeWithinTransaction(Method method, Class<?> targetClass, final InvocationCallback invocation) throws Throwable {

	// 获取事务属性，如果事务属性获取为空的话，那么当前这个方法就不是事务方法。（事务属性的获取里面是有事务解析器来进行解析的，这是题外话）
	final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
	//获取事务管理器，如果具体制定了事务管理器（这里说的执行是指@Transactional(rollbackFor = Exception.class,transactionManager='xxxx'),transactionManager所指定的事务管理器），那么就获取指定的；
	//如果事先没有添加指定任何transactionmanger，最终会从容器中按照类型获取一个默认的PlatformTransactionManager（这个就是我们在mainConfig中配置的，所以如果mainconfig中不配置一个默认的的话，就无法执行事务）
	//因为事务的执行，和回滚都是由事务管理器来进行操作的，下面会看到！
	final PlatformTransactionManager tm = determineTransactionManager(txAttr);
	//这个不重要，略
	final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);

	if (txAttr == null || !(tm instanceof CallbackPreferringPlatformTransactionManager)) {
		// Standard transaction demarcation with getTransaction and commit/rollback calls.
		// 通过我们的事务管理器，创建一个具体的事务，下面就是执行事务
		TransactionInfo txInfo = createTransactionIfNecessary(tm, txAttr, joinpointIdentification);
		Object retVal = null;
		try {
			// This is an around advice: Invoke the next interceptor in the chain.
			// This will normally result in a target object being invoked. （这通常会导致调用目标对象。）
			//通过事务，开始执行目标方法
			retVal = invocation.proceedWithInvocation();
		}
		catch (Throwable ex) {
			// target invocation exception
			//如果发生了异常，开始回滚
			completeTransactionAfterThrowing(txInfo, ex);
			throw ex;
		}
		finally {
			//清除事务信息
			cleanupTransactionInfo(txInfo);
		}
		//事务正常执行完了，提交事务
		commitTransactionAfterReturning(txInfo);
		//获取返回值，并返回
		return retVal;
	}else {
		// It's a CallbackPreferringPlatformTransactionManager: pass a TransactionCallback in.
		//....这里的代码省略了
	}
}

```

- 通过源码，我们可以很简单的看到事务的执行逻辑。这就是事务的具体执行步骤。
- 注意一点：真正的回滚与提交的时候，是事务管理器来执行的，方法拦截器只是用来拦截的。



### PlatformTransactionManager事务管理器用来干什么的？

这是我们配置的默认的事务管理器，当然我们也可以通过：

> @Transactional(rollbackFor = Exception.class,transactionManager='xxxx') 

指定其他的事务管理器。而不使用默认的事务管理器。但是当我们不指定其他的事务管理器的时候，这么默认的管理器就会生效了。

具体的可以参考本文上面说的，摘录部分内容如下：

```
//获取事务管理器，如果具体制定了事务管理器（这里说的执行是指@Transactional(rollbackFor = Exception.class,transactionManager='xxxx'),transactionManager所指定的事务管理器），那么就获取指定的；
//如果事先没有添加指定任何transactionmanger，最终会从容器中按照类型获取一个默认的PlatformTransactionManager（这个就是我们在mainConfig中配置的，所以如果mainconfig中不配置一个默认的的话，就无法执行事务）
//因为事务的执行，和回滚都是由事务管理器来进行操作的，下面会看到！
final PlatformTransactionManager tm = determineTransactionManager(txAttr);
```



### @Transactional的作用是什么，是怎么开启事务的。

- 他的作用简单的说就是：指定某个方式是一个事务方法！需要开启事务。
- 当然我们的目的不是这么肤浅的，我们要深入底层，看看这个注解的作用到底是什么。 其实在之前，已经解释过了这个注解。
- 这里简单的重复一下：我们知道开启事务之后，会注入事务增强器，事务拦截器和事务的属性信息（如果你不知道，请看本文的上面，都写了），其中事务的属性信息被增强器和拦截器所包含。
- 而拦截器是我们的事务执行的核心。属性信息就是拦截器的底层依赖。
- 在属性信息中，包含了事务的解析器。这个解析器，就是用来解析我们的事务注解（@Transactional）的，会从我们的事务注解中解析出来rollBackFor，指定的transactionManager等等。
- 解析出来之后，会将这些事务和事务管理器放在一起，封装成一个事务对象，用来具体的执行事务。
```
// 获取事务属性，如果事务属性获取为空的话，那么当前这个方法就不是事务方法。事务属性的获取里面是有事务解析器来进行解析的。
final TransactionAttribute txAttr = getTransactionAttributeSource().getTransactionAttribute(method, targetClass);
....

if (txAttr == null || !(tm instanceof CallbackPreferringPlatformTransactionManager)) {
		// Standard transaction demarcation with getTransaction and commit/rollback calls.
		// 通过我们的事务管理器，创建一个具体的事务，下面就是执行事务
		TransactionInfo txInfo = createTransactionIfNecessary(tm, txAttr, joinpointIdentification);
		Object retVal = null;
		try {}

.....


```

- 看看txAttr，就知道了。怎么生成的，以及在哪里调用的。




### 总结

至此，我们的事务管理就解析完了。和aop源码解析相比较，事务管理比较简单。因为aop涉及到很多很多的methodInterceptor(前置通知拦截，后置通知拦截，异常通知拦截等等)，而tx只涉及到一个MethodInterceptor（就是：TransactionInterceptor）。


事务的整体流程我们用一个图来描述吧。


![spirng-tx-事务的整体流程](http://youdao-note-images.oss-cn-hangzhou.aliyuncs.com/2019-07/e8f0b-spirng-tx-事务的整体流程.png)



## 三、致谢

- 感谢尚硅谷《spring源码分析》视频教程:https://www.bilibili.com/video/av32102436



