package example.hulk.com.apt_processor.bindview;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import example.hulk.com.apt_annotation.bindview.BindView;


/**
 * Created by fuzhi on 2019/5/15
 * 参考文章:https://www.jianshu.com/p/7af58e8e3e18
 */

@AutoService(Processor.class)//自动生成 javax.annotation.processing.IProcessor 文件
public class BindViewProcessor extends AbstractProcessor{

    private Messager messager;
    private Elements elementUtils;
    private Map<String, ClassCreatorProxy> proxyMap = new HashMap<>();

    /**
     * 初始化操作
     * @param processingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
    }

    /**
     * 返回此Processor支持的注解类型的名称。结果元素可能是某一受支持注释类型的规范(完全限定)名称。
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }

    /**
     * 返回此注解Processor支持的最新的源版本
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 处理器，处理具体注解
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE, "Printing: processing...");
        proxyMap.clear();
        // 1.获取代码中所有@BindView注解修饰的字段
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        for (Element element  : elements) {
            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            // fullClassName = 被BindView绑定的class = MainActivity
            String fullClassName = typeElement.getQualifiedName().toString();
            ClassCreatorProxy proxy = proxyMap.get(fullClassName);
            if(proxy == null) {
                proxy = new ClassCreatorProxy(elementUtils, typeElement);
                proxyMap.put(fullClassName, proxy);
            }
            BindView bindAnnotation = variableElement.getAnnotation(BindView.class);
            // 获取注解元素的值:id = value = @BindView(R.id.tv) -> R.id.tv
            int id = bindAnnotation.value();
            proxy.putElement(id, variableElement);
        }

        //通过遍历mProxyMap，创建java文件
//        for (String key: proxyMap.keySet()) {
//            ClassCreatorProxy proxyInfo = proxyMap.get(key);
//            try {
//                messager.printMessage(Diagnostic.Kind.NOTE, "--> create"
//                        + proxyInfo.getProxyClassFullName());
//                JavaFileObject jfo = processingEnv.getFiler().createSourceFile(proxyInfo.getProxyClassFullName()
//                , proxyInfo.getTypeElement());
//                Writer writer = jfo.openWriter();
//                writer.write(proxyInfo.generateJavaCode());
//                writer.flush();
//                writer.close();
//            } catch (IOException e) {
//                messager.printMessage(Diagnostic.Kind.NOTE, "--> create"
//                        + proxyInfo.getProxyClassFullName() + "error");
//            }
//            messager.printMessage(Diagnostic.Kind.NOTE, "process finish");
//        }

        //通过JavaPoet，创建java文件
        for (String key : proxyMap.keySet()) {
            ClassCreatorProxy proxyInfo = proxyMap.get(key);
            JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), proxyInfo.generateJavaCode()).build();
            try {
                //　生成文件
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "Printing: process finish ...");
        return true;
    }
}
