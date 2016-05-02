package searchengine ;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;

public class SearchEngine {

    public static void main(String[] args) {
        indexDirectory();

        String userInput = "";
        Scanner getInputWord = new Scanner(System.in);

        System.out.print("Please type a word to search for.\nInput: ");
        userInput = getInputWord.next();
        search(userInput);
        
        System.out.print("Search again? (type Y or N) Y = Yes, N = No: ");
        userInput = getInputWord.next();
        while (!userInput.equals("n") && !userInput.equals("N")) {
            System.out.print("Input: ");
            userInput = getInputWord.next();
            search(userInput);
            System.out.print("Search again? (Y or N): ");
            userInput = getInputWord.next();
        }
    }

    private static void indexDirectory() {
        //Apache Lucene Indexing Directory .txt files     
        try {
            Path path;
            path = Paths.get("C:\\Users\\USER\\Desktop\\index");
            Directory directory = FSDirectory.open(path);
            
            IndexWriterConfig config = new IndexWriterConfig(new SimpleAnalyzer());
            IndexWriter indexWriter = new IndexWriter(directory, config);
            indexWriter.deleteAll();
            File f = new File("C:\\seminar\\test_data"); // current directory     
            for (File file : f.listFiles()) {
                //System.out.println("indexed " + file.getCanonicalPath());
                Document doc = new Document();
                doc.add(new TextField("FileName", file.getName(), Store.YES));

                FileInputStream is = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuffer stringBuffer = new StringBuffer();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuffer.append(line).append("\n");
                }
                reader.close();
                doc.add(new TextField("contents", stringBuffer.toString(), Store.YES));
                indexWriter.addDocument(doc);
            }
            indexWriter.close();
            directory.close();
            System.out.println("indexing finished");
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private static void search(String text) {
        //Apache Lucene searching text inside .txt files
        try {
            Path path = Paths.get("C:\\Users\\USER\\Desktop\\index");

            Directory directory = FSDirectory.open(path);
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            FuzzyQuery query = new FuzzyQuery(new Term("contents", text), 2);
            TopDocs topDocs = indexSearcher.search(query, 10);

            int i = 0;
            if (topDocs.totalHits > 0) {
                System.out.println("Found " + topDocs.totalHits + " result(s).");
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    
                    Document document = indexSearcher.doc(scoreDoc.doc);
                    System.out.println("Result #" + i + " " + document.get("FileName"));
                    i = i + 1;
                 String[] list ;  

                 Scanner txtscan = new Scanner(new File("C:\\seminar\\test_data\\"+document.get("FileName")));
            int line = 0;      
       while(txtscan.hasNextLine()){
              String str = txtscan.nextLine();
              line ++ ;
             
                 if(str.indexOf(text) != -1){
           
                     int x = str.indexOf(text) ; 
                     int allx = str.length();
                     String prString = str.replace(text, "("+text+")");
                     if(x<=30 ){
                         if(allx<=50){
                      prString = prString.substring(0, allx) ; 
                    System.out.println("line number #"+line +":  "+ prString);
                         }
                         else {
                    prString = prString.substring(0, 50) ; 
                    System.out.println("line number #"+line +": "+ prString);
                         }
                      
                     }
                   
                     else  {
                        
                      prString = prString.substring(x-20, allx) ; 
                      System.out.println("line number #"+line +":  "+ prString);
                       
                         
                     }

                   
                    
                  }
                }
                    
                }
            } else {
                System.out.println("No maches found!");
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
