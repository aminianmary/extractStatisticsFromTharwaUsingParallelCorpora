package ExtractStatsUsingParallelCorpora;

/**
 *
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Hashtable;

/**
 * @author badrashiny
 * Jan 28, 2016
 */
public class TransDictCreator {

    /**
     * @throws IOException
     *
     */
    public TransDictCreator(String phraseT1Path, String phraseT2Path, String transDictPath) throws IOException {
        createTransDict(phraseT1Path,  phraseT2Path, transDictPath);
    }
    private void createTransDict(String phraseT1Path, String phraseT2Path, String transDictPath) throws IOException{
        System.out.println("Loading the first phrase table...");
        Hashtable<String,String> phraseTable1=loadRefHash(phraseT1Path);

        InputStream pt2 = new FileInputStream(phraseT2Path);
        BufferedReader readbuffer = new BufferedReader(findEncoding(pt2));

        OutputStreamWriter fstream=new OutputStreamWriter(new FileOutputStream(transDictPath), "UTF-8");
        BufferedWriter out = new BufferedWriter(fstream);

        String strRead;
        while ((strRead=readbuffer.readLine())!=null){
            strRead=strRead.trim();
            if(strRead.isEmpty()){//skip empty lines
                continue;
            }
            String[]temp=strRead.split("\\s+");//EGY/POS/EN
            if(temp.length!=3){
                continue;
            }
            String key=temp[2]+"@@"+temp[1];//EN@@POS
            String egy=temp[0];//EGY

            String currentValue=phraseTable1.get(key);
            if(currentValue!=null){//i.e. matched
                String[] MSAValues=currentValue.split(";;");
                for(int i=0;i<MSAValues.length;i++){//create EGY/POS/EN/MSA
                    out.write(egy+"\t"+temp[1]+"\t"+temp[2]+"\t"+MSAValues[i]+"\n");
                }
            }
        }
        out.close();
        readbuffer.close();
    }

    private Hashtable<String,String> loadRefHash(String path) throws IOException{
        Hashtable<String,String> refHash = new Hashtable<String,String>(2000000);
        InputStream ref = new FileInputStream(path);
        BufferedReader readbuffer = new BufferedReader(findEncoding(ref));
        String strRead;

        while ((strRead=readbuffer.readLine())!=null){
            strRead=strRead.trim();
            if(strRead.isEmpty()){//skip empty lines
                continue;
            }
            String[]temp=strRead.split("\\s+");//MSA/POS/EN
            if(temp.length!=3){
                continue;
            }
            String key=temp[2]+"@@"+temp[1];//EN@@POS
            String value=temp[0];//MSA
            String oldValue=refHash.get(key);
            if(oldValue==null){//i.e. new record
                refHash.put(key, value);
            }else{//i.e. already added value
                refHash.put(key, oldValue+";;"+value);
            }
        }
        readbuffer.close();
        return refHash;
    }
    /** find the correct encoding of the input stream and skip the BOM header
     * @param in :the InputStream of the input file
     * @return : InputStreamReader to the input stream with the correct encoding after skipping the BOM
     * @throws IOException
     *
     *
     */
    private InputStreamReader findEncoding(InputStream in) throws IOException{
        if (!in.markSupported()){
            in = new BufferedInputStream(in);
        }
        in.mark(3);
        int byte1 = in.read();
        int byte2 = in.read();
        if (byte1 == 0xFF && byte2 == 0xFE){//i.e. UTF-16LE
            return new InputStreamReader(in, "UTF-16LE");
        }else if (byte1 == 0xFF && byte2 == 0xFE){//i.e. UTF-16BE
            return new InputStreamReader(in, "UTF-16BE");
        }else{
            int byte3 = in.read();
            if (byte1 == 0xEF && byte2 == 0xBB && byte3 == 0xBF){//i.e. UTF-8 with BOM
                return new InputStreamReader(in, "UTF-8");
            }else{ //i.e. UTF-8 without BOM
                in.reset();
                return new InputStreamReader(in, "UTF-8");
            }
        }
    }
//	public static void main(String[] args) throws Exception{
//		String[] arg={"parallel_dic_MSA","parallel_dic_EGY","transDict.txt"};
//		main1(arg);
//	}
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String phraseT1Path=args[0];
        String phraseT2Path=args[1];
        String transDictPath=args[2];

        TransDictCreator tDict =new TransDictCreator( phraseT1Path,  phraseT2Path, transDictPath);


    }

}
