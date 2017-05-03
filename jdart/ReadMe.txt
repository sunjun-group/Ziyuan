项目中最重要的四处环境配置地方为：
1、libs\jpf.properties
2、jdart\jpf.properties 
	以上文件中只需要注意*classpath变量的设置

3、代码中传入的String[]参数，设置了目标类、目标方法等具体设置，具体例子参考SimulateJPFTest

4、libs\z3-4.3.2-x64-win\bin\*.dll 需要位于java.library.path路径中，我现在是将路径直接添加到系统的path变量中，因为代码中指定java.library.path好像没有生效，还是从path变量中寻找
