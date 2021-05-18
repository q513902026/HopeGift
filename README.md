激活码插件

# 命令
## 已完成:
### 普通命令 权限:hopegift.use
```
/cdk [cdk]  激活CDK
```
### 管理员命令 权限:hopegift.admin
```
/hopegift reloadConfig  重载配置
/hopegift import [激活码类型名称] [文件名称]  从文件中导入CDK到配置文件中 使其对应相应的激活码奖品类型
/hopegift export [激活码类型名称]                    从配置文件中导出特定激活码奖品类型所属的CDK
/hopegift enable [激活码类型名称]                   从配置文件中启用特定激活码奖品
/hopegift disable [激活码类型名称]                  从内存中卸载特定激活码奖品
/hopegift states                                  显示所有激活码奖品的启用状态和使用次数统计
/hopegift state [激活码类型名称]                      显示特定激活码奖品的启用状态和使用次数统计
/hopegift help                                      显示所有注册的管理命令
```
## 配置文件
### cdk.yml
用于存储cdk的使用
### gift.yml
用于存储激活码奖品类型
### config.yml
用于存储基础设定

cdk.yml 格式讲解
```
TEST_1: #激活码奖品类型名称
  21245456: false  #激活码
TEST_2:
  TESTCODE: false
```
当激活码的对应值为false时代表未使用

gift.yml 格式讲解
```
#根节点
gift:
  # 激活码奖品类型名称
  TEST_1:
    # 激活码奖品使用类型 REPEAT代表每个玩家对每个激活码可使用一次 UNIQUE代表每个激活码对应使用一次 不对应玩家
    type: REPEAT
   # 激活码奖品结果类型 COMMANDS 代表执行控制台命令 目前只有COMMANDS
    result: COMMANDS
   # REPEAT激活码奖品使用类型专有 存储使用了该类型的玩家名称
    USER_LIST: []
   # 激活码奖品结果执行  当COMMANDS代表可以执行的命令 %PLAYER%用来指代兑换玩家的名字
    value:
      - gamemode 1 %PLAYER%
```
config.yml
```
version: 1 #配置文件版本标识
prefix: "&d[CDK]&c" #游戏中通知的前缀
log: true #是否保存操作日志 目前还未使用
enableGifts: #启用的激活码奖品 不在这个列表中的激活码不会载入内存
  - TEST_1
  - TEST_2
lastUpdateCDK: 0000-00-00 #上次更新CDK的时候 暂未使用
```