package example.hulk.com.apt_processor.test;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import example.hulk.com.apt_annotation.test.Test;

@AutoService(Processor.class)//自动生成 javax.annotation.processing.IProcessor 文件
public class AnnotationProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elementUtils;
    private Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Test.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        filer = processingEnv.getFiler();                   // 文件相关的辅助类
        elementUtils = processingEnv.getElementUtils();     // 元素相关的辅助类
        messager = processingEnv.getMessager();             // 日志相关的辅助类

        MethodSpec main = MethodSpec.methodBuilder("main")                  // 创建main方法
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)                   // 添加修饰符
                .addJavadoc("@ 此类由apt自动生成")                                  // 定义注释
                .returns(void.class)                                              // 定义返回参数
                .addParameter(String[].class, "args")                       // 定义方法参数
                .addStatement("$T.out.println($S)", System.class, "hello world")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")             // 创建HelloWorld类
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)                      // 添加修饰符
                .addMethod(main)                                                    // 添加方法
                .addJavadoc("@ 此类由apt自动生成")                                    // 定义注解
                .build();

        JavaFile javaFile = JavaFile.builder("com.apt.devil", helloWorld).build();
        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
