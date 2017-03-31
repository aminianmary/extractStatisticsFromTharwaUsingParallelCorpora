///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////  Supporting Functions to Preprocess Text and doing some utilities  ///////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


package utilities;
import java.io.*;
import java.util.Set;

/**
 * Created by monadiab on 1/8/16.
 */
public class Preprocessing {

    public static void main(String args[])
    {
        /**
         args[0] = Alef-Ya normalized
         args[1] = diacritized (boolean)
         args[2]= input file to preprocess
         args[3]= preprocessed file
         */
        boolean isNormalized= Boolean.parseBoolean(args[0]);
        boolean isDiacratized= Boolean.parseBoolean(args[1]);
        String inputFile= args[2];
        String preprocessedFile= args[3];

        preprocessMonolingualData(isNormalized, isDiacratized, inputFile, preprocessedFile);

        //preprocessAlignedData(isNormalized, isDiacratized, inputFile, preprocessedFile);

    }

    public static void preprocessMonolingualData(boolean isNormalized,  boolean isDiacratized, String inputFile,  String preprocessedFile)
    {
        try {
            BufferedReader inputFileReader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter preprocessedFileWriter= new BufferedWriter(new FileWriter(preprocessedFile));

            String lineToRead= "";
            int senCounter=0;

            while ((lineToRead= inputFileReader.readLine())!= null)
            {
                senCounter++;

                if (senCounter%100000==0)
                    System.out.println(senCounter);

                lineToRead= lineToRead.trim();
                String[] splitLine= lineToRead.split(" ");
                String preprocessedSen= "";
                for (String word: splitLine)
                {
                    String token= word;
                    String pos="";

                    if (word.contains("@@pos@@")) {
                        token = word.split("@@pos@@")[0];
                        pos = word.split("@@pos@@")[1];
                    }

                    if (isNormalized==true)
                    {
                        if (isDiacratized==false) {
                            token = (Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(token)));
                        }
                        else {
                            token = (Preprocessing.normalizeAlefYa(token));
                        }
                    }
                    else
                    {
                        if (isDiacratized== false)
                        {
                            token= (Preprocessing.undiacratize(token));
                        }
                        else
                        {
                            token= (token);
                        }
                    }

                    if (word.contains("@@pos@@"))
                        preprocessedSen+= token+"@@pos@@"+pos+" ";
                    else
                        preprocessedSen+= token+" ";
                }

                preprocessedFileWriter.write(preprocessedSen.trim() + "\n");
            }

            preprocessedFileWriter.flush();
            preprocessedFileWriter.close();

        }catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }


    public static void preprocessAlignedData(boolean isNormalized,  boolean isDiacratized, String inputFile,  String preprocessedFile)
    {
        try {
            BufferedReader inputFileReader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter preprocessedFileWriter= new BufferedWriter(new FileWriter(preprocessedFile));

            String lineToRead= "";
            int senCounter=0;

            while ((lineToRead= inputFileReader.readLine())!= null)
            {
                senCounter++;

                if (senCounter%100000==0)
                    System.out.println(senCounter);

                lineToRead= lineToRead.trim();
                String[] splitLine= lineToRead.split("\t");

                String ArabicWord= splitLine[0];
                String pos= splitLine[1];
                String En= splitLine[2];

                if (isNormalized==true)
                {
                    if (isDiacratized==false) {
                        ArabicWord = (Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(ArabicWord)));
                    }
                    else {
                        ArabicWord = (Preprocessing.normalizeAlefYa(ArabicWord));
                    }
                }
                else
                {
                    if (isDiacratized== false)
                    {
                        ArabicWord= (Preprocessing.undiacratize(ArabicWord));
                    }
                    else
                    {
                        ArabicWord= (ArabicWord);
                    }
                }

                preprocessedFileWriter.write(ArabicWord+"\t"+ pos+"\t"+En + "\n");
            }

            preprocessedFileWriter.flush();
            preprocessedFileWriter.close();

        }catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public static String getSafeBW(String BW)
    {
        String tempSafeBW="";
        tempSafeBW=BW;

        if (tempSafeBW.contains("|"))
            tempSafeBW= tempSafeBW.replace("|","M");
        if(tempSafeBW.contains("<"))
            tempSafeBW=tempSafeBW.replace("<","I");
        if(tempSafeBW.contains(">"))
            tempSafeBW=tempSafeBW.replace(">","O");
        if(tempSafeBW.contains("&"))
            tempSafeBW=tempSafeBW.replace("&","W");
        if(tempSafeBW.contains("}"))
            tempSafeBW=tempSafeBW.replace("}","Q");
        if(tempSafeBW.contains("*"))
            tempSafeBW=tempSafeBW.replace("*","V");
        if(tempSafeBW.contains("$"))
            tempSafeBW=tempSafeBW.replace("$","c");
        if(tempSafeBW.contains("'"))
            tempSafeBW=tempSafeBW.replace("'","C");
        if(tempSafeBW.contains("{"))
            tempSafeBW=tempSafeBW.replace("{","L");
        if(tempSafeBW.contains("`"))
            tempSafeBW=tempSafeBW.replace("`","e");

        return tempSafeBW;
    }


    public static String normalizeAlefYa (String originalWord)
    {
        return originalWord.replace("|","A").replace("<","A").replace(">","A").replace("{","A").replace("`","A").replace("Y","y");
    }

    public static String normalizeAlefYa_in_SafeBW (String originalWord)
    {
        return originalWord.replace("M","A").replace("I","A").replace("O","A").replace("L","A").replace("e","A").replace("Y","y");
    }

    public static String undiacratize (String originalWord)
    {
        return originalWord.replaceAll("[FNKauio~]", "");

    }

    public static String removeInsideWordPlus(String str){
        String cleanedStr=str;
        boolean hasLeadingPlus=false;
        boolean hasTailingPlus=false;
        if (str.charAt(0)=='+')
        {
            hasLeadingPlus=true;
            cleanedStr=str.substring(1,str.length());
        }
        if(str.charAt(str.length()-1)=='+'){
            hasTailingPlus=true;
            cleanedStr=str.substring(0,str.length()-1);
        }
        if (cleanedStr.contains("+"))
            cleanedStr=cleanedStr.replace("+","");

        if (hasLeadingPlus==true)
            cleanedStr="+"+cleanedStr;
        if(hasTailingPlus==true)
            cleanedStr=cleanedStr+"+";

        return cleanedStr;
    }

    public static Set intersect(Set coll1, Set coll2) {
        Set intersection = coll1;
        intersection.retainAll(coll2);
        return intersection;
    }

    public static Set nonOverLap(Set coll1, Set coll2) {
        Set nonOverlap = coll1;
        nonOverlap.removeAll(intersect(coll1, coll2));
        return nonOverlap;
    }

    public static String remove_last_vowel(String input)
    {
        String output= input;

        String diacRegEx="[uiao~FKN`\u064F\u0650\u064E\u0652\u0651\u064B\u064D\u064C\u0670]"+"+$";  //bw or utf8 diacs
        output = output.replaceAll(diacRegEx,"");
        return output;
    }

    /*
    public static String undoSafeBW(String input)
    {
        String tempOut="";
        tempOut=input;

        if (tempOut.contains("M"))
            tempOut= tempOut.replace("M","|");
        if(tempOut.contains("I"))
            tempOut=tempOut.replace("I","<");
        if(tempOut.contains("O"))
            tempOut=tempOut.replace("O",">");
        if(tempOut.contains("W"))
            tempOut=tempOut.replace("W","&");
        if(tempOut.contains("Q"))
            tempOut=tempOut.replace("Q","}");
        if(tempOut.contains("V"))
            tempOut=tempOut.replace("V","*");
        if(tempOut.contains("c"))
            tempOut=tempOut.replace("c","$");
        if(tempOut.contains("C"))
            tempOut=tempOut.replace("C","'");
        if(tempOut.contains("L"))
            tempOut=tempOut.replace("L","{");
        if(tempOut.contains("e"))
            tempOut=tempOut.replace("e","`");

        return tempOut;
    }
    */
}
