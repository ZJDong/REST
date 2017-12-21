package com.skin.finder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;  
import java.util.Arrays;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.alibaba.fastjson.JSON;
public class TaskAssignAndResultCollection {
	
	private final static int DEFAULT_THREAD_NUM = 5;  
	 
    private int threadNum = DEFAULT_THREAD_NUM;  
    private Worker[] threads = null;  
 
    public TaskAssignAndResultCollection(int threadNum) {  
        super();  
        if (threadNum == 0) {  
            threadNum = DEFAULT_THREAD_NUM;  
        } else {  
            this.threadNum = threadNum;  
        }  
 
    }  
 
    public ArrayList<String> processStringBatchly(  
    		RevCommit[] datas) {  
 
        if (threads == null) {  
            synchronized (this) {  
                threads = new Worker[threadNum];  
                  
                for(int i = 0 ; i < threadNum; i++) {  
                    threads[i] = new Worker();  
                }  
            }  
        }  
 
        // 怎么把domainName分配给线程， 让它们自己运行去？平均分配，  
        int domainSize = datas.length;  
        int domainNamePerThread = domainSize / threadNum;  
        int leftDomainName = domainSize % threadNum;   
          
        List<RevCommit> listDomainName = Arrays.asList(datas);  
 
        //先每个线程平均地分domainNamePerThread个DomainName，  
        int endIndex = 0;  
        for (int i=0; i<threadNum; i++) {  
            int beginIndex = i * domainNamePerThread;  
            int step = domainNamePerThread;  
            endIndex = beginIndex + step;   
            List<RevCommit> subDomainNames = new ArrayList<RevCommit>(listDomainName.subList(beginIndex, endIndex));  
              
            threads[i].setDomainNameList(subDomainNames);  
        }  
          
        //然后，再把剩下的逐个分配。  
        for(int i=0; i< leftDomainName; i++) {  
            threads[i].addDomainName(listDomainName.get(endIndex + i));  
        }  
          
        for(Worker thread : threads ) {  
            thread.start();  
            try {  
                thread.join();  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
          
        ArrayList<String> totalResult = new ArrayList<String>(); 
          
        for(Worker thread : threads) {  
            totalResult.addAll(thread.getResultCollector());  
        }  
          
        return totalResult;  
    }  
      
      
    public static void main(String[] args) {  
        String[] datas = new String[] {"baidu.com", "baiduaa.com","sohu.com", "163.com", "iteye.com", "sohu.com", "163.com", "iteye.com",
        		"163.com", "iteye.com", "sohu.com", "163.com", "iteye.com", "163.com", "iteye.com", "sohu.com", "163.com", "iteye.com", "163.com"
        		, "iteye.com", "sohu.com", "163.com", "iteye.com", "163.com", "iteye.com", "sohu.com", "163.com", "iteye.com", 
        		"163.com", "iteye.com", "sohu.com", "163.com", "iteye.com", "163.com", "iteye.com", "sohu.com", "163.com", "iteye.com", "163.com", "iteye.com", "sohu.com", "163.com", "iteye.com", "haha.com"};  
//        System.out.print(datas.length+"输入所要建立的worker数量：");
        
//        try {
//			sayHello("3191377947cd5ed80f7d298627fb65a8ce80ed7b");
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        
//        String result="";//访问返回结果
//        BufferedReader read=null;//读取访问结果
//        String urlNameString = "https://api.github.com/repos/ZJDong/SocketServer/commits" ;
//        URL realUrl;
		try {
//			realUrl = new URL(urlNameString);
//	        // 打开和URL之间的连接
//	        URLConnection connection = realUrl.openConnection();
//	        // 设置通用的请求属性
//	        connection.setRequestProperty("accept", "*/*");
//	        connection.setRequestProperty("connection", "Keep-Alive");
//	        connection.setRequestProperty("user-agent",
//	                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			connection.connect();
//			Map<String, List<String>> map = connection.getHeaderFields();
//			// 遍历所有的响应头字段，获取到cookies等
////	         for (String key : map.keySet()) {
////	             System.out.println(key + "--->" + map.get(key));
////	         }
//	         // 定义 BufferedReader输入流来读取URL的响应
//	         read = new BufferedReader(new InputStreamReader(
//	                 connection.getInputStream(),"UTF-8"));
//	         String line;//循环读取
//	         while ((line = read.readLine()) != null) {
//	             result += line;
//	         }
//	         List<String> strArray = JSON.parseArray(result, String.class);
//	         String[] str=new String[strArray.size()];
//	         for(int i=0;i<strArray.size();i++){
//	        	 Map maps = (Map)JSON.parse(strArray.get(i)); 
//	        	 str[i]=maps.get("html_url").toString();
////	        	 System.out.println(maps.get("html_url"));
//	         }
	         System.out.println("Please enter the number of worker to be set up :");
	         Scanner scan = new Scanner(System.in);
	         //the number
	         String reads = scan.nextLine();
//	         System.out.println("Please enter the address of github you want to test :");
//	         Scanner scan2 = new Scanner(System.in);
//	         //the address
//	         String reads2 = scan2.nextLine();
	         long startTime = System.currentTimeMillis(); 
	         
	         
	         TaskAssignAndResultCollection c = new TaskAssignAndResultCollection(Integer.parseInt(reads));  
	         //this method you must input the address
	         RevCommit[] sayHello = sayHello("");
	         
	         
	         
	         ArrayList<String> resultCollector = c.processStringBatchly(sayHello);  
	         
	         long endTime = System.currentTimeMillis();  
	         float seconds = (endTime - startTime) / 1000F; 
	         System.out.println("All time : "+seconds);
	         c.showMsg(resultCollector);  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

         
        
        

    }  
    
    
    
    //git method
    private static final String LOCAL_CLONE_REPO = "C:/Users/EDD/Desktop/董知见分布式文件系统/第五版12121423/date2/";
    private static String branchName;
    public static RevCommit[] sayHello(String name) throws Exception{
//    	File directory = new File("");
//        String LOCAL_CLONE_REPO = directory.getCanonicalPath();
        Integer sum = 0;
        //"https://github.com/ZJDong/SocketServer/"
//        Git.cloneRepository().setURI("https://github.com/hmkcode/Java")
//                .setDirectory(new File(LOCAL_CLONE_REPO)).setCloneAllBranches(true)
//                .call();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(LOCAL_CLONE_REPO + ".git"))
                .readEnvironment().findGitDir().build();
        System.out.println("iefsfd"+repository);
        Git git = new Git(repository);

        RevWalk walk = new RevWalk(repository);

        List<Ref> branches = git.branchList().call();
        
//        RevCommit[] rev=new RevCommit[34];
        for (Ref branch : branches) {
        	branchName = branch.getName();

            System.out.println("Commits of branch: " + branch.getName());
            System.out.println("-------------------------------------");

            Iterable<RevCommit> commits = git.log().call();
            
            ArrayList<RevCommit> arr=new ArrayList<RevCommit>();
            for (RevCommit commit : commits) {
            	arr.add(commit);
            }
            RevCommit[] rev=new RevCommit[arr.size()];
            for (int i=0;i<arr.size();i++) {
            	rev[i]=arr.get(i);
            }
            System.out.println("Total number :"+arr.size());
            return rev;
        }
        System.out.println("kkk"+sum);
        return null;
    }
 
    private void showMsg(ArrayList<String> result) {
    	double total=0;
    	double count=0;
    	int workers=0;
        for(int i=0;i<result.size();i++){
        	if(i%3==0){
        		total=total+Double.parseDouble(result.get(i));
        	}
        	if(i%3==1){
        		count=count+Double.parseDouble(result.get(i));
        	}
        	if(i%3==2){
        		workers=workers+1;
        		System.out.println("Worker number :"+workers+" , "+"Spend time : "+result.get(i));
        	}
        	
        }
        System.out.println("Total workers :"+workers);
//        System.out.println("total :"+total);
//        System.out.println("count :"+count);
        DecimalFormat    df   = new DecimalFormat("######0.00"); 
        System.out.println("averageComplexity :"+df.format(count/total));
    }  
      
      
      
}  
 
class Worker extends Thread {
	
    private static String branchName;
    private List<RevCommit> datas;  
    
    List<Ref> branches;
    private ArrayList<String> resultCollector = new ArrayList<String>(); 
    
    private ArrayList<Integer> averageCC = new ArrayList<Integer>();
    String result="";//访问返回结果
    BufferedReader read=null;//读取访问结果
    public void run() {  
    	long startTime = System.currentTimeMillis(); 
    	Integer sum = 0;
    		
//            String result = d + "@";  

            // 建立实际的连接
            try {
            	int file = getFile(datas);
            	resultCollector.add(String.valueOf(datas.size()));
            	resultCollector.add(String.valueOf(file));
//            	String urlNameString = "http://192.168.1.124:8080/JavaTeam/Test/testBackResult.html" + "?d=" + d;
//                URL realUrl = new URL(urlNameString);
//                // 打开和URL之间的连接
//                URLConnection connection = realUrl.openConnection();
//                // 设置通用的请求属性
//                connection.setRequestProperty("accept", "*/*");
//                connection.setRequestProperty("connection", "Keep-Alive");
//                connection.setRequestProperty("user-agent",
//                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//				connection.connect();
//				Map<String, List<String>> map = connection.getHeaderFields();
//				// 遍历所有的响应头字段，获取到cookies等
////	             for (String key : map.keySet()) {
////	                 System.out.println(key + "--->" + map.get(key));
////	             }
//	             // 定义 BufferedReader输入流来读取URL的响应
//	             read = new BufferedReader(new InputStreamReader(
//	                     connection.getInputStream(),"UTF-8"));
//	             String line;//循环读取
	             
//	             while ((line = read.readLine()) != null) {
//	                 result += line;
//	             }
//	             System.out.println("返回的结果"+result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        long endTime = System.currentTimeMillis();  
        float seconds = (endTime - startTime) / 1000F; 
        resultCollector.add(String.valueOf(seconds));
        
//        System.out.println("time:"+seconds);
        
    }  
 
    public void setDomainNameList(List<RevCommit> subDomainNames) {  
        datas = subDomainNames;  
    }  
      
    public void addDomainName(RevCommit domainName) {  
        if (datas == null ) {  
            datas = new ArrayList<RevCommit>();  
        }  
        datas.add(domainName);  
    }  
 
    public ArrayList<String> getResultCollector() {  
        return resultCollector;  
    }  
    
    public ArrayList<Integer> getAverageCC() {  
        return averageCC;  
    }  

    private static final String LOCAL_CLONE_REPO = "C:/Users/EDD/Desktop/董知见分布式文件系统/第五版12121423/date2/";
    public static int getFile(List<RevCommit> commit){
    	Integer sum = 0;
    	Integer total=0;
    	
    	try {
//    		File directory = new File("");
//            String LOCAL_CLONE_REPO = directory.getCanonicalPath();
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository repository = builder.setGitDir(new File(LOCAL_CLONE_REPO + ".git"))
			        .readEnvironment().findGitDir().build();
			RevWalk walk = new RevWalk(repository);
			for(int i=0;i<commit.size();i++){
				RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.get(i).getName()));
				for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {
				    if (e.getKey().startsWith(Constants.R_HEADS) && walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
				        String foundInBranch = e.getValue().getName();
				        if ("refs/heads/master".equals(foundInBranch)) {
				            RevTree tree = targetCommit.getTree();
				            TreeWalk treeWalk = new TreeWalk(repository);
				            treeWalk.addTree(tree);
				            treeWalk.setRecursive(false);
				            System.out.println("Commit found: " + commit.get(i).getName());
				            while (treeWalk.next()) {
				                ObjectId objectId = treeWalk.getObjectId(0);
				                ObjectLoader loader = repository.open(objectId);
				                File newFile = new File("tempReadFile");
				                loader.copyTo(new FileOutputStream(newFile));
//	                        CyclometicCalculation cal = new CyclometicCalculation();
				                sum += calculateCC(newFile);
				                total += calculateCC(newFile);
				            }
				            break;
				        }
				    }
				}
				 System.out.println("Complexity :"+(sum<0?0:sum));
				 sum=0;
			}
         
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return total<0?0:total;
    }
    public static int calculateCC(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> ifs = Arrays.asList("if", "?");
        List<String> loops = Arrays.asList("while", "case", "for", "switch", "do");
        List<String> returns = Arrays.asList("return");
        List<String> conditions = Arrays.asList("&&", "||", "or", "and", "xor");
        int complexity = 0;
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] wordsInLine = line.split(" |;|\t");
            boolean ifFlag = false;
            for (String perWord : wordsInLine) {
                if (ifs.contains(perWord)) {
                    ifFlag = true;
                    complexity++;
                }
                if (loops.contains(perWord)) {
                    complexity++;
                }
                if (conditions.contains(perWord)) {
                    if (ifFlag) {
                        complexity += 2;
                    } else {
                        complexity++;
                    }
                }
                if (returns.contains(perWord)) {
                    complexity--;
                }
            }
        }
        return complexity;
    }
}
