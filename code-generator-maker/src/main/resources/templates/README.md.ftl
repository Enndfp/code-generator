# ${name}

> ${description}
>
> 作者: ${author}
>
> 版本: ${version}
>
> 创建时间: ${.now}
>
> 源码地址: [定制化代码生成项目](https://github.com/Enndfp/code-generator)
>
> ${name} 旨在简化模板生成过程,提供快速、高效的代码生成方式，使开发人员能够更专注于业务逻辑和功能开发，用户通过命令行界面交互输入几个参数就能生成定制化的代码。
>
> 当然，如果你喜欢 ${name} 的话，不妨给我们的项目点个 star ⭐️！您的支持是我们不断进步的动力，也能让更多的开发者受益。感谢您的支持！


## 项目使用

需要执行相关脚本文件，需要安装 java 1.8+ 版本


如在项目目录下执行脚本文件

Linux
```bash
./generator
```

Windows
```bash
./generator.bat
```
否则需要拼全脚本路径
```bash
路径/generator <命令> <选项参数>
```


#### 1. 生成代码命令说明:

用于生成 ACM 模板代码的命令 是最重要的一个命令

```bash
./generator generate  <选项参数>
```

示例:
``` bash
./generator generate <#list modelConfig.models as modelInfo>-${modelInfo.abbr} </#list>
```

#### 2. 参数说明:
<#list modelConfig.models as modelInfo>
##### (${modelInfo?index + 1}) ${modelInfo.fieldName}

    类型: ${modelInfo.type}

    描述: ${modelInfo.description}

    默认值: ${modelInfo.defaultValue?c}

    命令缩写: -${modelInfo.abbr}
</#list>

<strong>注: forcedInteractive 配置项若开启则是强交互式，关闭则是弱交互式</strong>

#### 3. 查看配置命令说明:

输出 generate 参数类型等信息

```bash
./generator config
```

#### 4. 查看文件目录说明:

输出文件目录信息

```bash
./generator list
```

${name} 犹如彩虹一般绚丽多彩，为开发者带来了代码编写的新体验。它不仅简化了开发过程，还为项目注入了更多活力和创造力。让我们与 ${name} 一起探索编码世界的奇妙之处吧! 🌈✨

欢迎体验 ${name}，有任何建议或疑问，请随时联系我们。您可以通过电子邮件 enndfp@qq.com 与我们取得联系。让我们一起为代码生成而创造更美好的未来吧!