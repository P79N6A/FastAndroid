package apache.artemis_compiler.proxy;

import com.apache.artemis_annotation.BindOnClick;
import com.apache.artemis_annotation.BindViewById;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.STATIC;

/**
 * Created by Jerry on 2019/2/4.
 *  调试方法参考 https://www.jianshu.com/p/80a14bc35000
 */
@AutoService(Processor.class)
public class ProxyToolProcessor extends AbstractProcessor {
    private Filer mFiler; //文件相关的辅助类
    private Elements mElementUtils; //元素相关的辅助类
    private Messager mMessager; //日志相关的辅助类

    /**
     * 处理器的初始化方法，可以获取相关的工具类
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
    }

    /**
     * key: com.apache.fastandroid.MainActivity
     * value:ProxyClass
     */
    private Map<String, ProxyClass> mProxyClassMap = new HashMap<>();

    /**
     * 处理器的主方法，用于扫描处理注解，生成java文件
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //处理被ViewById注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(BindViewById.class)) {
            if (!isValid(BindViewById.class, "fields", element)) {
                return true;
            }
            parseViewById(element);
        }

        //处理被Click注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(BindOnClick.class)) {
            if (!isValid(BindOnClick.class, "methods", element)) {
                return true;
            }
            try {
                parseClick(element);
            } catch (IllegalArgumentException e) {
                error(element, e.getMessage());
                return true;
            }
        }

        //为每个宿主类生成所对应的代理类
        for (ProxyClass proxyClass_ : mProxyClassMap.values()) {
            try {
                proxyClass_.generateProxy().writeTo(mFiler);
            } catch (IOException e) {
                error(null, e.getMessage());
            }
        }
        mProxyClassMap.clear();

        return true;
    }

    /**
     * 处理ViewById注解
     *
     * @param element
     */
    private void parseViewById(Element element) {
        ProxyClass proxyClass = getProxyClass(element);
        //把被注解的view对象封装成一个model，放入代理类的集合中
        FieldViewBinding bindView = new FieldViewBinding(element);
        proxyClass.add(bindView);
    }

    /**
     * 处理Click注解
     *
     * @param element
     */
    private void parseClick(Element element) throws IllegalArgumentException {
        ProxyClass proxyClass = getProxyClass(element);
        //把被注解的view对象封装成一个model，放入代理类的集合中
        MethodViewBinding bindView = new MethodViewBinding(element);
        proxyClass.add(bindView);
    }


    /**
     *  生成或获取注解元素所对应的ProxyClass类
     */
    private ProxyClass getProxyClass(Element element) {
        //被注解的变量所在的类
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        String qualifiedName = classElement.getQualifiedName().toString();
        ProxyClass proxyClass = mProxyClassMap.get(qualifiedName);

        System.out.println("getProxyClass element = "+element
                +", element simpleName = "+element.getSimpleName()
        +", kind = "+ element.getKind() +", Modifier = "+ element.getModifiers());

        System.out.println("getProxyClass 父类名字 = "+qualifiedName
        +", enclosing element = "+ element.getEnclosingElement() +", EnclosedElements = "+ element.getEnclosedElements());

        if (proxyClass == null) {
            //生成每个宿主类所对应的代理类，后面用于生产java文件
            proxyClass = new ProxyClass(classElement, mElementUtils);
            mProxyClassMap.put(qualifiedName, proxyClass);
        }
        return proxyClass;
    }


    private boolean isValid(Class<? extends Annotation> annotationClass, String targetThing, Element element) {
        boolean isVaild = true;

        //获取变量的所在的父元素，肯能是类、接口、枚举
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        //父元素的全限定名
        String qualifiedName = enclosingElement.getQualifiedName().toString();

        // 所在的类不能是private或static修饰
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isVaild = false;
        }

        // 父元素必须是类，而不能是接口或枚举
        if (enclosingElement.getKind() != ElementKind.CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), targetThing, enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            isVaild = false;
        }

        //不能在Android框架层注解
        if (qualifiedName.startsWith("android.")) {
            error(element, "@%s-annotated class incorrectly in Android framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }
        //不能在java框架层注解
        if (qualifiedName.startsWith("java.")) {
            error(element, "@%s-annotated class incorrectly in Java framework package. (%s)",
                    annotationClass.getSimpleName(), qualifiedName);
            return false;
        }

        return isVaild;
    }

    /**
     *  指定哪些注解应该被注解处理器注册
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindViewById.class.getName());
        types.add(BindOnClick.class.getName());
        return types;
    }

    /**
     *  用来指定你使用的 java 版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private void error(Element e, String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }
}
