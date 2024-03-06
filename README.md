<img src="https://img.enndfp.cn/202402221905897.jpg" alt="hive" style="zoom: 50%;" />

**简体中文** | [English](README-EN.md) 



<div align="center">
<h1>🌟 HiveGenerator - 定制化代码生成 🚀</h1>
</div>

<div align="center">
<b>🛠️ 定制化代码生成项目</b>
</div>
<div align="center">
<img src="https://img.shields.io/badge/Java-1.8-orange"/>
<img src="https://img.shields.io/badge/SpringBoot-2.x-green"/>
<img src="https://img.shields.io/badge/Redis-compatible-yellowgreen"/>
<img src="https://img.shields.io/badge/XXL_JOB-2.4.0-blue"/>
<img src="https://img.shields.io/badge/FreeMarker-2.3.32-lightblue"/>
<img src="https://img.shields.io/badge/Mybatis-2.2.2-yellow"/>
<img src="https://img.shields.io/badge/MybatisPlus-3.5.2-brightgreen"/>
<img src="https://img.shields.io/badge/knife4j-4.4.0-blueviolet"/>
<img src="https://img.shields.io/badge/TencentCOS-5.6.89-blue"/>
<img src="https://img.shields.io/badge/Picocli-4.7.5-orange"/>
<img src="https://img.shields.io/badge/Hutool-5.8.16-brightgreen"/>
<img src="https://img.shields.io/badge/Commons_Collections-4.4-green"/>
<img src="https://img.shields.io/badge/Caffeine-2.9.3-yellowgreen"/>
<img src="https://img.shields.io/badge/Lombok-1.18.30-purple"/>
<img src="https://img.shields.io/badge/JUnit-4.13.2-red"/>
</div>

## 📖 项目简介

HiveGenerator 是一个分阶段开发的定制化代码生成器平台。它不仅旨在提高个人和团队的开发效率，减少重复性编码工作，而且通过社区的力量，促进代码生成器的共享和协作。项目分为三个主要阶段：

1. **本地代码生成器** 🖥️：基于命令行的脚手架工具，根据用户交互式输入快速生成特定代码。
2. **代码生成器制作工具** 🛠️：将常用项目代码快速转化为代码生成器，提升工作效率。
3. **在线代码生成器平台** 🌐：支持制作、发布、在线使用和共享代码生成器，促进协作开发。

## 🔍 解决的问题

- **提高效率** ⏱️：自动化生成重复性代码片段，解决效率低下问题。
- **定制化需求** 🎨：满足开发中的特定需求，提供定制化解决方案。
- **促进协作** 👥：线上平台支持代码生成器的共享与协作，提升团队协作效率。

## 📐 项目设计

### 🔧 代码生成器核心原理

**参数 + 模板文件 = 生成的完整代码**

比如参数：

```java
author = Enndfp
```

模板文件代码：

```java
-----------
I am ${author}
-----------
```

将参数注入到模板文件中，得到生成的完整代码：

```java
-----------
I am Enndfp
-----------
```

如果想要使用这套模板生成其他的代码，只需要改变参数的值即可，而不需要改变模板文件

---

### 🖥️ 第一阶段 - 本地代码生成器

目标：开发一个本地（离线）的代码生成器，实现一个简易的 Java ACM 模板项目的定制化生成

#### 业务流程

1）准备用于制作代码生成器的原始代码（比如 Java ACM 模板项目），用于后续生成

2）开发者基于原始代码，设置参数，编写动态模板

3）制作可交互的命令行工具，支持用户输入参数，得到代码生成器 Jar 包

4）使用者得到代码生成器 Jar 包，执行程序并输入参数，从而生成完整代码

#### 流程图

![image-20240222212102219](https://img.enndfp.cn/202402231454988.png)

#### 实现思路

1）先根据本地项目，扫描文件树，实现同样的静态代码生成

2）根据本地项目，预设部分动态参数、编写模板文件，能够传入配置对象进行生成

3）制作可交互的命令行工具，接收用户输入的参数，并动态生成代码

4）封装制作代码生成器 Jar 包文件，并简化使用命令

---

### 🛠️ 第二阶段 - 代码生成器制作工具

目标：开发一个本地的代码生成器制作工具，能够快速将一个项目制作为可以动态定制部分内容的代码生成器

#### 业务流程

1）准备用于制作代码生成器的原始代码，用于后续生成

2）开发者基于原始代码，**使用代码生成器制作工具**，来快速设置参数、生成动态模板

3）**使用代码生成器制作工具**，动态生成代码生成器 Jar 包

4）使用者得到代码生成器 Jar 包，执行程序并输入参数，从而生成完整代码

相较于第一阶段的业务流程，本阶段完成后可以直接使用代码生成器制作工具来快速将固定的项目代码改造为可以定制的动态模板，并自动生成命令行工具 Jar 包

#### 流程图

![image-20240226231813219](https://img.enndfp.cn/202402262318331.png)

![image-20240222211124687](https://img.enndfp.cn/202402231454221.png)

#### 实现思路

1）使用独立空间来存储管理要生成的原始文件、动态模板文件等

2）使用配置文件来记录要生成的参数和模板文件信息、自定义配置等**元信息**

3）代码生成器制作工具需要有多种可单独或组合使用的功能，比如从原始文件中抽取参数、动态生成命令行工具、打 Jar 包等

---

### 🌐 第三阶段 - 在线代码生成器平台

目标：开发一个在线代码生成器平台，可以理解为代码生成器的**应用市场**。所有人都能发布、使用、甚至是在线制作自己的代码生成器。

#### 业务流程

1）获取用于制作代码生成器的原始代码（手动准备或者远程拉取代码）

2）开发者基于原始代码，使用**在线代码生成器制作工具**，来快速制作代码生成器

3）开发者发布代码生成器至平台

4）使用者在平台上搜索代码生成器，支持在线使用或者下载离线 Jar 包（甚至还可以支持接口调用）

#### 流程图

![image-20240306190155663](https://img.enndfp.cn/202403061902772.png)

#### 实现思路

1）使用 Web 开发框架实现代码生成器信息的增删改查

2）将本地的配置和文件**上云**，存储到数据库、对象存储等云服务

3）通过可视化界面来操作第二阶段的代码生成器制作工具，复用阶段二的成果