这是Spring面试中的常见易混淆点，下面用简明的方式帮你理清 **BeanFactory** 和 **FactoryBean** 的关系及区别：

---

## 1. BeanFactory
- **BeanFactory** 是 Spring 的**顶级容器接口**，类似于一个“Bean工厂”，用于管理和获取Bean实例。
- 主要负责**实例化、定位、配置Bean**，是IoC的核心接口。
- 常见实现：`DefaultListableBeanFactory`、`XmlBeanFactory`等。

---

## 2. FactoryBean
- **FactoryBean** 是一个**用于定制Bean实例化逻辑的接口**，可以把复杂的Bean创建过程封装起来。
- 通过实现`org.springframework.beans.factory.FactoryBean<T>`接口，开发者可以定义如何创建Bean。
- 当Spring容器检测到某个Bean实现了`FactoryBean`接口，**通过getObject()方法获取Bean实例**。

---

## 3. 容易混淆点及关系

| 对象        | 作用                   | 你获取的是什么            |
| ----------- | ---------------------- | ------------------------- |
| BeanFactory | 容器本身，管理Bean     | 获取各种普通Bean          |
| FactoryBean | 特殊Bean，生产其他Bean | 获取FactoryBean生产的Bean |

- **BeanFactory** 是容器，**FactoryBean** 是用户自定义的“工厂型Bean”。
- **FactoryBean** 本身是一个Bean，但它的特殊之处在于：**通过它的getObject()方法返回的对象才是你最终想要的Bean**。
- 如果你想获取FactoryBean本身，需要在beanId前加`&`（如`&myFactoryBean`）。

---

## 4. 场景举例

- 比如定义一个连接池或者代理对象时，通常用FactoryBean来定制创建逻辑。
- Spring内置的`ProxyFactoryBean`、`SqlSessionFactoryBean`等都是FactoryBean的实现。

---

## 5. 总结一句话

**BeanFactory**是Spring的核心IoC容器接口，用于管理所有Bean；**FactoryBean**是一个特殊的Bean，通过它可以定制和包装复杂的Bean实例化过程。

---

如需代码示例或更深原理可以随时追问！