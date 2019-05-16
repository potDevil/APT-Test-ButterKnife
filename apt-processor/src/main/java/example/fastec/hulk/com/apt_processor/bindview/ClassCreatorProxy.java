package example.fastec.hulk.com.apt_processor.bindview;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * Created by fuzhi on 2019/5/16
 * BindView 创建java代理类
 */
public class ClassCreatorProxy {
    private String bindingClassName;
    private String packageName;
    private TypeElement typeElement;
    private Map<Integer, VariableElement> variableElementMap = new HashMap<>();

    public ClassCreatorProxy(Elements elementUtils, TypeElement typeElement) {
        this.typeElement = typeElement;
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = typeElement.getSimpleName().toString();
        this.packageName = packageName;
        this.bindingClassName = className + "_ViewBinding";
    }

    public void putElement(int id, VariableElement element) {
        variableElementMap.put(id, element);
    }

    /**
     * 通过JavaPoet创建class
     * @return
     */
    public TypeSpec generateJavaCode() {
//        String strPackage = "package " + packageName + ";\n\n";
//        String strImport = "import example.fastec.hulk.com.apt_library.*;\n";
        TypeSpec bindingClass = TypeSpec.classBuilder(bindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods())
                .build();
        return bindingClass;
    }

    /**
     * 通过JavaPoet创建Method
     * @return
     */
    public MethodSpec generateMethods() {
        ClassName host = ClassName.bestGuess(typeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(host, "host");

        for (int id : variableElementMap.keySet()) {
            VariableElement element = variableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("host." + name + "=" + "(" + type
                    + ")(((android.app.Activity)host).findViewById(" + id + "));");
        }
        return methodBuilder.build();
    }

    /**
     * 通过拼接字符串创建class
     * @return
     */
    public String generateJavaCode2() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import example.fastec.hulk.com.apt_library.*;\n");
        builder.append('\n');
        builder.append("public class ").append(bindingClassName);
        builder.append(" {\n");

        generateMethods2(builder);
        builder.append('\n');
        builder.append("}\n");
        return builder.toString();
    }


    /**
     * 通过拼接字符串创建method
     * @param builder
     */
    public void generateMethods2(StringBuilder builder) {
        builder.append("public void bind(" + typeElement.getQualifiedName() + " host ) {\n");
        for (int id : variableElementMap.keySet()) {
            VariableElement element = variableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));\n");
        }
        builder.append("  }\n");
    }

    public String getProxyClassFullName() {
        return packageName + "." + bindingClassName;
    }

    public TypeElement getTypeElement() {
        return typeElement;
    }

    public String getPackageName() {
        return packageName;
    }
}
