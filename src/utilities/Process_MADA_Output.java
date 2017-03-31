package utilities;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: maryam
 * Date: 5/3/14
 * Time: 6:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class Process_MADA_Output
{

    public static void main (String[] args) throws Exception
    {
        /*
        args[0]= MADA extracted feats file path
        args[1]= Tokenized Output file Path
        args[2]= normalized?
        args[3]= lemmatized?
        args[4]= diacratized?
        args[5]= add POS?
         */
        String MADAOutputPath= args[0];
        String tokenizedOutputPath=args[1];
        boolean isNormalized= Boolean.parseBoolean(args[2]);
        boolean isLemmatized= Boolean.parseBoolean(args[3]);
        boolean isDiacratized= Boolean.parseBoolean(args[4]);
        boolean hasPOS= Boolean.parseBoolean(args[5]);

        String normalized="0";
        String lemmatized="tok";
        String diacratized="undiac";
        String POSTagged="0";

        if (isNormalized==true)
            normalized="AlefYaNorm";

        if (isLemmatized==true)
            lemmatized="lem";

        if (isDiacratized==true)
            diacratized="diac";

        if (hasPOS==true)
            POSTagged="pos";

        process_MADA_Output(MADAOutputPath,tokenizedOutputPath+"."+normalized+"."+lemmatized+"."+diacratized+"."+POSTagged,isNormalized,isLemmatized,isDiacratized,hasPOS);

    }

    public static void process_MADA_Output (String MADAOutputFilePath, String TokenizedOutputFilePath,
                                            boolean AlefYaNormalized, boolean lemmatized, boolean diacratized, boolean hasPOS) throws Exception
    {

        BufferedReader MADAOutputReader= new BufferedReader(new InputStreamReader(new FileInputStream(MADAOutputFilePath)));
        BufferedWriter TokenizedOutputFileWriter= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TokenizedOutputFilePath)));

        String MADALineToRead="";
        String factor_del = Character.toString((char) 183);

        while ((MADALineToRead= MADAOutputReader.readLine())!= null)
        {
            //comment
            if (MADALineToRead.startsWith("#"))
                continue;
            //empty line
            if (MADALineToRead.equals(""))
            {
                TokenizedOutputFileWriter.write("\n");
                continue;
            }

            /*Extracted features are organized in this order:
            1- Original_word 2-normalized_word 3-lemma 4-BW
            5-enc0 6-enc1 7-enc2
            8-prc0 9-prc1 10-prc2 11-prc3
            12- POS
            */
            String[] splitedLine= MADALineToRead.split("\t");
            String wordToWrite="";
            String originalWord=splitedLine[0];
            String lemma= splitedLine[2];
            if (lemma.contains("-"))
                lemma= lemma.split("-")[0];
            else
                lemma= lemma.split("_")[0];
            String BW= splitedLine[3];

            if (AlefYaNormalized== true)
            {
                //Alef-Ya normalized
                originalWord=normalizeAlefYa (originalWord);
                lemma= normalizeAlefYa(lemma);
                BW= normalizeAlefYa(BW);
            }
            String enc0= splitedLine[4];
            String enc1= splitedLine[5];
            String enc2= splitedLine[6];

            String prc0= splitedLine[7];
            String prc1= splitedLine[8];
            String prc2= splitedLine[9];
            String prc3= splitedLine[10];
            String POS= splitedLine[11];

            /////////special characters////////////////////
            //////////////////////////////////////////////
            if (lemma.equals("UNK"))
                lemma= originalWord;
            if (BW.equals("UNK"))
                BW= originalWord;
            if (originalWord.equals("/") || originalWord.equals("+") || originalWord.equals("_") || originalWord.equals("-"))
            {
                TokenizedOutputFileWriter.write(originalWord+" ");
                continue;
            }

            ///////////////////////////////////////////
            //////////////////////////////////////////
            //  Regular words ///////////////////////

            List<String> splittedTokens= new ArrayList<String>(Arrays.asList(BW.split("\\+")));
            int numExistingPRC= getNumeberOfprc(prc0,prc1,prc2,prc3);
            int numExistingENC= getNumeberOfenc(enc0,enc1,enc2);

            System.out.println("numExistingPRC "+numExistingPRC);
            System.out.println("numExistingENC "+numExistingENC);
            System.out.println("splittedTokens size "+ splittedTokens.size());


            if (splittedTokens.size() < numExistingPRC+numExistingENC+1)
                System.out.println("Number of Tokens Mismatch");

            else {
                //Proclitics
                for (int i = 0; i < numExistingPRC; i++) {
                    String prcLex = splittedTokens.get(0).split("/")[0];
                    if (diacratized == true)
                        wordToWrite += getSafeBW(prcLex) + "+ ";
                    else
                        wordToWrite += getSafeBW(undiacratize(prcLex) + "+ ");

                    splittedTokens.remove(0);
                }

                //Base word
                String normalizedLex = "";
                if (splittedTokens.size() == numExistingENC + 1) {
                    //Base word contains one token
                    normalizedLex = splittedTokens.get(0).split("/")[0];
                    splittedTokens.remove(0);
                } else {
                    //Base word contains more than one token
                    int numberTokens= splittedTokens.size()-numExistingENC;
                    for (int i = 0; i < numberTokens; i++)
                    {
                        String token = splittedTokens.get(0).split("/")[0];
                        if (!token.equals("(null)"))
                            normalizedLex += getSafeBW(token);
                        splittedTokens.remove(0);
                    }
                }
                System.out.println(normalizedLex);
                System.out.println(POS);

                if (lemmatized == true)
                {
                    //lemmatized
                    if (diacratized == true)
                    {
                        //lemmatized-diacratized
                        wordToWrite += getSafeBW(lemma)+"|"+POS;
                    } else {
                        //lemmatized-undiacratized
                        wordToWrite += getSafeBW(undiacratize(lemma))+"|"+POS;

                    }
                } else {
                    //tokenized
                    if (diacratized == true) {
                        //tokenized-diacratized
                        wordToWrite += getSafeBW(normalizedLex)+"|"+POS;
                    } else {
                        //tokenized-undiacratized
                        wordToWrite += getSafeBW(undiacratize(normalizedLex))+"|"+POS;
                    }

                }

                //Enclitics
                for (int i = 0; i < numExistingENC; i++) {
                    String encLex = splittedTokens.get(0).split("/")[0];
                    if (diacratized == true)
                        wordToWrite += " +"+getSafeBW(encLex);
                    else
                        wordToWrite += " +"+getSafeBW(undiacratize(encLex));

                    splittedTokens.remove(0);
                }
            }

            TokenizedOutputFileWriter.write(wordToWrite + " ");
        }

        TokenizedOutputFileWriter.flush();
        TokenizedOutputFileWriter.close();

    }


    public static int getNumeberOfprc (String prc0, String prc1, String prc2, String prc3){
        int numExistingPrc=0;
        if (prc0.equals("0") || prc0.equals("na") || prc0.equals("UNK"))
            prc0="0";
        else
            prc0="1";
        if (prc1.equals("0") || prc1.equals("na") || prc1.equals("UNK"))
            prc1="0";
        else
            prc1="1";
        if (prc2.equals("0") || prc2.equals("na") || prc2.equals("UNK"))
            prc2="0";
        else
            prc2="1";
        if (prc3.equals("0") || prc3.equals("na") || prc3.equals("UNK"))
            prc3="0";
        else
            prc3="1";
        String prcs= prc0+prc1+prc2+prc3;
        if (prcs.equals("0000"))
            numExistingPrc=0;
        else if (prcs.equals("0100") || prcs.equals("1000") || prcs.equals("0001") || prcs.equals("0010"))
            numExistingPrc=1;
        else if (prcs.equals("0011") || prcs.equals("1001") || prcs.equals("1100") || prcs.equals("0110") || prcs.equals("1010") || prcs.equals("0101"))
            numExistingPrc=2;
        else if (prcs.equals("0111") || prcs.equals("1011") || prcs.equals("1101") || prcs.equals("1110"))
            numExistingPrc=3;
        else if (prcs.equals("1111"))
            numExistingPrc=4;
        return numExistingPrc;
    }


    public static int getNumeberOfenc (String enc0, String enc1, String enc2){
        int numExistingPrc=0;
        if (enc0.equals("0") || enc0.equals("na") || enc0.equals("UNK"))
            enc0="0";
        else
            enc0="1";
        if (enc1.equals("0") || enc1.equals("na") || enc1.equals("UNK"))
            enc1="0";
        else
            enc1="1";
        if (enc2.equals("0") || enc2.equals("na") || enc2.equals("UNK"))
            enc2="0";
        else
            enc2="1";
        String encs= enc0+enc1+enc2;
        if (encs.equals("000"))
            numExistingPrc=0;
        else if (encs.equals("100") || encs.equals("010") || encs.equals("001"))
            numExistingPrc=1;
        else if (encs.equals("011") || encs.equals("101") || encs.equals("110"))
            numExistingPrc=2;
        else if (encs.equals("111"))
            numExistingPrc=3;
        return numExistingPrc;
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


    public static String undiacratize (String originalWord)
    {
        return originalWord.replaceAll("[FNKauio~]", "");

    }

}
