package apache.artemis_compiler;

import com.apache.artemis_annotation.FindId;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

/**
 * Created by Jerry on 2019/1/28.
 */
public class TCompiler extends AbstractProcessor {

    private Messager messager;

    private Elements mElements;

    private Filer mFiler;


    private Map<String,ProxyInfo> mProxyMap = new HashMap<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        messager = processingEnvironment.getMessager();
        mElements = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        collectionInfo(roundEnvironment);
        generateClass();
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> hashSet = new LinkedHashSet<>();
        hashSet.add(FindId.class.getCanonicalName());
        System.out.println("getSupportedAnnotationTypes annotation name = "+FindId.class.getCanonicalName() );
        return hashSet;
    }

    private void collectionInfo(RoundEnvironment roundEnvironment){
        mProxyMap.clear();
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(FindId.class);
        for (Element element : elements) {
            int value = element.getAnnotation(FindId.class).value();
            //处理类注解
            if (element.getKind().isClass()){
                System.out.println("collectionInfo element kind isClass ");
                TypeElement typeElement = (TypeElement) element;
                //类的完整路径
                String qualifiedName = typeElement.getQualifiedName().toString();
                String clzName = typeElement.getSimpleName().toString();
                String packageName = mElements.getPackageOf(element).getQualifiedName().toString();
                System.out.println("qualifiedName = "+qualifiedName +", clzName = "+clzName +", packageName = "+packageName);
               /* ProxyInfo proxyInfo = mProxyMap.get(qualifiedName);
                if (proxyInfo == null){
                    proxyInfo = new ProxyInfo();
                    mProxyMap.put(qualifiedName,proxyInfo);
                }*/
                ProxyInfo proxyInfo = new ProxyInfo();
                proxyInfo.setValue(value);
                proxyInfo.setPkgName(packageName);
                proxyInfo.setTypeElement(typeElement);

                mProxyMap.put(qualifiedName,proxyInfo);
            }
            //处理成员变量注解
            else if (element.getKind().isField()){
                System.out.println("collectionInfo element kind isField ");
                VariableElement variableElement = (VariableElement) element;
                //代表类
                TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                String qualifiedName = typeElement.getQualifiedName().toString();
                System.out.println("qualifiedName = "+ qualifiedName +", simpleName = "+ typeElement.getSimpleName().toString());
                ProxyInfo proxyInfo = mProxyMap.get(qualifiedName);
                if (proxyInfo == null){
                    proxyInfo = new ProxyInfo();
                    mProxyMap.put(qualifiedName,proxyInfo);
                }
                proxyInfo.getInjectElements().put(value,variableElement);
            }else {
                System.out.println("not class or field");
                continue;
            }
        }
    }

    private void generateClass(){
        for (String key : mProxyMap.keySet()) {
            ProxyInfo proxyInfo = mProxyMap.get(key);
            JavaFileObject sourceFile;

            try {
                sourceFile = mFiler.createSourceFile(proxyInfo.getProxyClassFullName(), proxyInfo.getTypeElement());
                Writer writer = sourceFile.openWriter();
                writer.write(proxyInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
