<img src="https://img.enndfp.cn/202402221905897.jpg" alt="hive" style="zoom: 50%;" />

[简体中文](README.md) | **English** 



<div align="center">
<h1>🌟 HiveGenerator - customized code generation 🚀</h1>
</div>

<div align="center">
<b>🛠️ customized code generation project</b>
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


## 📖 Project Description

HiveGenerator is a customized code generator platform developed in stages. It not only aims to improve the development efficiency of individuals and teams and reduce repetitive coding work, but also promotes the sharing and collaboration of code generators through the power of the community. The project is divided into three main phases:

1. **Local Code Generator** 🖥️: A command line-based scaffolding tool that quickly generates specific code based on user interactive input.
2. **Code Generator Making Tool** 🛠️: Quickly convert commonly used project codes into code generators to improve work efficiency.
3. **Online Code Generator Platform** 🌐: Supports the production, publishing, online use and sharing of code generators to promote collaborative development.

## 🔍 Problems solved

- **Improve efficiency** ⏱️: Automatically generate repetitive code snippets to solve the problem of low efficiency.
- **Customized requirements** 🎨: Meet specific needs in development and provide customized solutions.
- **Promote collaboration** 👥: The online platform supports the sharing and collaboration of code generators to improve team collaboration efficiency.

## 📐 Project Design

### 🔧 Core principles of code generator

**Parameters + template file = generated complete code**

For example parameters:

```java
author=Enndfp
```

Template file code:

```java
----------
I am ${author}
----------
```

Inject parameters into the template file to get the complete generated code:

```java
----------
I am Enndfp
----------
```

If you want to use this set of templates to generate other code, you only need to change the parameter values without changing the template file.

---

### 🖥️ Phase 1 - Native Code Generator

Goal: Develop a local (offline) code generator to achieve customized generation of a simple Java ACM template project

#### Business Process

1) Prepare the original code used to make the code generator (such as the Java ACM template project) for subsequent generation

2) Developers set parameters and write dynamic templates based on the original code

3) Create an interactive command line tool that supports user input parameters and obtains the code generator Jar package

4) The user gets the code generator Jar package, executes the program and enters parameters to generate complete code

#### Flowchart

![image-20240222212102219](https://img.enndfp.cn/202402231454988.png)

#### Implementation ideas

1) First scan the file tree based on the local project to achieve the same static code generation

2) Based on the local project, preset some dynamic parameters, write template files, and pass in the configuration object for generation

3) Make an interactive command line tool, receive user input parameters, and dynamically generate code

4) Encapsulate the code generator Jar package file and simplify the use of commands

---

### 🛠️ Phase 2 - Code Generator Making Tool

Goal: Develop a local code generator production tool that can quickly turn a project into a code generator that can dynamically customize part of the content.

#### Business Process

1) Prepare the original code used to make the code generator for subsequent generation

2) Based on the original code, developers use code generator tools to quickly set parameters and generate dynamic templates

3) **Use the code generator production tool** to dynamically generate the code generator Jar package

4) The user gets the code generator Jar package, executes the program and enters parameters to generate complete code

Compared with the business process in the first stage, after the completion of this stage, you can directly use the code generator production tool to quickly transform the fixed project code into a customizable dynamic template, and automatically generate the command line tool Jar package.

#### Flowchart

![image-20240226231813219](https://img.enndfp.cn/202402262318331.png)

![image-20240222211124687](https://img.enndfp.cn/202402231454221.png)

#### Implementation ideas

1) Use independent space to store and manage original files, dynamic template files, etc. to be generated

2) Use the configuration file to record the parameters and template file information to be generated, custom configuration and other **meta-information**

3) Code generator production tools need to have a variety of functions that can be used individually or in combination, such as extracting parameters from original files, dynamically generating command line tools, packaging Jar packages, etc.

---

### 🌐 Phase 3 - Online Code Generator Platform

Goal: Develop an online code generator platform, which can be understood as an **application market** for code generators. Anyone can publish, use, and even create their own code generator online.

#### Business Process

1) Obtain the original code used to make the code generator (manually prepare or pull the code remotely)

2) Developers use the **Online Code Generator Making Tool** to quickly create a code generator based on the original code.

3) Developers publish code generators to the platform

4) Users search for code generators on the platform, which supports online use or downloading offline Jar packages (it even supports interface calls)

#### Flowchart

![image-20240306190155663](https://img.enndfp.cn/202403061902772.png)

#### Implementation ideas

1) Use the web development framework to implement addition, deletion, modification and query of code generator information

2) Move local configurations and files to the cloud and store them in cloud services such as databases and object storage

3) Use the visual interface to operate the code generator production tool in the second stage and reuse the results of the second stage.