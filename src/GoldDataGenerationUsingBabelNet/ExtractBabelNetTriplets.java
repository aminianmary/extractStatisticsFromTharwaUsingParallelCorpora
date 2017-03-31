package GoldDataGenerationUsingBabelNet;

import it.uniroma1.lcl.babelnet.*;
import it.uniroma1.lcl.babelnet.iterators.BabelSynsetIterator;
import it.uniroma1.lcl.jlt.util.Language;
import utilities.Preprocessing;

import java.io.*;
import java.util.HashSet;
import java.util.List;

/**
 * Created by monadiab on 4/29/16.
 */
public class ExtractBabelNetTriplets {

    public static TripletDic babelNet_En_MSA_EGY_common_with_parallel_vocab_dic = new TripletDic();

    public static void main(String[] args) throws IOException{

        HashSet<String> En_vocab= utils.getEnVocab(args[0]);
        En_vocab= utils.updateEnVocab(En_vocab, args[1]);
        HashSet<String> MSA_vocab= utils.getArabicVocab(args[2]);
        HashSet<String> EGY_vocab= utils.getArabicVocab(args[3]);

        String transDictPath= args[4];
        String tripletFilePath= args[5];
        String transDict_babelNet_common_triplets= args[6];
        String babelNet_transDict_common_triplets= args[7];


        //getBabelNetDics (En_vocab, MSA_vocab, EGY_vocab, tripletFilePath);
        loadBabelNetTriplets(En_vocab, MSA_vocab, EGY_vocab,tripletFilePath);
        TripletDic transDict= createTransDictDic (transDictPath);

        babelNet_En_MSA_EGY_common_with_parallel_vocab_dic.compare(transDict, transDict_babelNet_common_triplets,"wb");
        transDict.compare(babelNet_En_MSA_EGY_common_with_parallel_vocab_dic, babelNet_transDict_common_triplets,"wt");
    }


    public static void getBabelNetDics (HashSet<String> En_vocab,HashSet<String> MSA_vocab, HashSet<String> EGY_vocab) throws IOException
    {
        BufferedWriter BabelNetTripletsWriter= new BufferedWriter(new OutputStreamWriter(new FileOutputStream("BabelNet_triplets")));

        BabelNet bn = BabelNet.getInstance();
        BabelSynsetIterator bsi = bn.getSynsetIterator();
        int total_synset_counter = 0;
        int qualified_synste_counter=0;

        while (bsi.hasNext()) {
            if (total_synset_counter % 10000 == 0)
                System.out.println(total_synset_counter);

            BabelSynset synset = bsi.next();
            total_synset_counter++;
            if (synset.getSynsetType().equals(BabelSynsetType.CONCEPT)) {

                String synset_id = synset.getId().toString();

                List<BabelSense> MSA_senses = synset.getSenses(Language.AR);
                List<BabelSense> En_senses = synset.getSenses(Language.EN);
                List<BabelSense> EGY_senses = synset.getSenses(Language.ARZ);

                if (MSA_senses.size()>0 && En_senses.size()>0 && EGY_senses.size()>0)
                {

                    HashSet<String> MSA_senses_lemma = utils.getArabicSensesLemma(MSA_senses);
                    HashSet<String> EGY_senses_lemma = utils.getArabicSensesLemma(EGY_senses);
                    HashSet<String> En_senses_lemma = utils.getEnSensesLemma(En_senses);

                    if (MSA_senses_lemma.size()>0 && EGY_senses_lemma.size()>0 && En_senses_lemma.size()>0)
                    {
                        qualified_synste_counter++;
                        for (String En_sense: En_senses_lemma)
                        {
                            for (String MSA_sense: MSA_senses_lemma)
                            {
                                for (String EGY_sense: EGY_senses_lemma)
                                {
                                    //found a qualified BabelNet triplet
                                    BabelNetTripletsWriter.write(synset_id + "\t" + En_sense + "\t" + MSA_sense +"\t"+ EGY_sense+"\n");

                                    //compare it with parallel corpora vocab
                                    if (En_vocab.contains(En_sense))
                                    {
                                        if (MSA_vocab.contains(MSA_sense))
                                        {
                                            if (EGY_vocab.contains(EGY_sense)) {
                                                babelNet_En_MSA_EGY_common_with_parallel_vocab_dic.update_all_dics(En_sense, MSA_sense, EGY_sense);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Number of Extracted Synsets: "+  qualified_synste_counter);
        BabelNetTripletsWriter.flush();
        BabelNetTripletsWriter.close();
    }


    public static void loadBabelNetTriplets (HashSet<String> En_vocab,HashSet<String> MSA_vocab, HashSet<String> EGY_vocab,
                                             String filePath) throws IOException
    {
        System.out.println("Loading BabelNet Triplets from BabelNet_triplets file...");
        BufferedReader reader= new BufferedReader(new FileReader(filePath));

        BufferedWriter En_sim_MSA_sim_EGY_sim_writer= new BufferedWriter(new FileWriter("En_sim_MSA_sim_EGY_sim_wp"));
        BufferedWriter En_sim_MSA_sim_EGY_diff_writer= new BufferedWriter(new FileWriter("En_sim_MSA_sim_EGY_diff_wp"));

        BufferedWriter En_sim_MSA_diff_EGY_sim_writer= new BufferedWriter(new FileWriter("En_sim_MSA_diff_EGY_sim_wp"));
        BufferedWriter En_sim_MSA_diff_EGY_diff_writer= new BufferedWriter(new FileWriter("En_sim_MSA_diff_EGY_diff_wp"));

        BufferedWriter En_diff_MSA_diff_EGY_sim_writer= new BufferedWriter(new FileWriter("En_diff_MSA_diff_EGY_sim_wp"));
        BufferedWriter En_diff_MSA_diff_EGY_diff_writer= new BufferedWriter(new FileWriter("En_diff_MSA_diff_EGY_diff_wp"));
        BufferedWriter En_diff_MSA_sim_EGY_sim_writer= new BufferedWriter(new FileWriter("En_diff_MSA_sim_EGY_sim_wp"));
        BufferedWriter En_diff_MSA_sim_EGY_diff_writer= new BufferedWriter(new FileWriter("En_diff_MSA_sim_EGY_diff_wp"));

        String line2read="";
        int En_sim_MSA_sim_EGY_sim_wp=0;
        HashSet<String> En_sim_MSA_sim_EGY_sim_wp_uniq_entries= new HashSet<String>();
        int counter=0;

        while ((line2read= reader.readLine())!= null)
        {
            counter++;
            if (counter%1000==0)
                System.out.println(counter);

            line2read= line2read.trim();
            String[] splitLine= line2read.split("\t");
            String sysnset_id= splitLine[0];
            String En= splitLine[1];
            String En_POS= splitLine[2];
            String MSA= splitLine[3];
            String MSA_POS= splitLine[4];
            String EGY= splitLine[5];
            String EGY_POS= splitLine[6];

            //compare it with parallel corpora vocab
            if (En_vocab.contains(En))
            {
                if (MSA_vocab.contains(MSA))
                {
                    if (EGY_vocab.contains(EGY)) {
                        babelNet_En_MSA_EGY_common_with_parallel_vocab_dic.update_all_dics(En, MSA, EGY);
                        En_sim_MSA_sim_EGY_sim_wp_uniq_entries.add(En + "\t" + En_POS + "\t" + MSA + "\t" + MSA_POS + "\t" + EGY + "\t" + EGY_POS);
                        En_sim_MSA_sim_EGY_sim_wp++;
                    }
                    else
                        En_sim_MSA_sim_EGY_diff_writer.write(line2read+"\n");
                }
                else
                {
                    if (EGY_vocab.contains(EGY))
                        En_sim_MSA_diff_EGY_sim_writer.write(line2read+"\n");
                    else
                        En_sim_MSA_diff_EGY_diff_writer.write(line2read+"\n");
                }

            }
            else
            {
                if (MSA_vocab.contains(MSA))
                {
                    if (EGY_vocab.contains(EGY))
                        En_diff_MSA_sim_EGY_sim_writer.write(line2read+"\n");
                    else
                        En_diff_MSA_sim_EGY_diff_writer.write(line2read+"\n");
                }
                else
                {
                    if (EGY_vocab.contains(EGY))
                        En_diff_MSA_diff_EGY_sim_writer.write(line2read+"\n");
                    else
                        En_diff_MSA_diff_EGY_diff_writer.write(line2read+"\n");
                }
            }

        }

        System.out.print("En_sim_MSA_sim_EGY_sim_wp: "+ En_sim_MSA_sim_EGY_sim_wp_uniq_entries.size() +"\n");

        for (String triplet:En_sim_MSA_sim_EGY_sim_wp_uniq_entries)
        {
            En_sim_MSA_sim_EGY_sim_writer.write(triplet+"\n");
        }

        En_sim_MSA_sim_EGY_sim_writer.flush();
        En_sim_MSA_sim_EGY_sim_writer.close();
        En_sim_MSA_sim_EGY_diff_writer.flush();
        En_sim_MSA_sim_EGY_diff_writer.close();
        En_sim_MSA_diff_EGY_sim_writer.flush();
        En_sim_MSA_diff_EGY_sim_writer.close();
        En_sim_MSA_diff_EGY_diff_writer.flush();
        En_sim_MSA_diff_EGY_diff_writer.close();
        En_diff_MSA_diff_EGY_sim_writer.flush();
        En_diff_MSA_diff_EGY_sim_writer.close();
        En_diff_MSA_diff_EGY_diff_writer.flush();
        En_diff_MSA_diff_EGY_diff_writer.close();
        En_diff_MSA_sim_EGY_sim_writer.flush();
        En_diff_MSA_sim_EGY_sim_writer.close();
        En_diff_MSA_sim_EGY_diff_writer.flush();
        En_diff_MSA_sim_EGY_diff_writer.close();

    }


    public static TripletDic createTransDictDic (String transDictPath) throws IOException
    {
        HashSet<String> transDict_uniq_entries= new HashSet<String>();
        BufferedWriter transDict_uniq_entries_writer= new BufferedWriter(new FileWriter("transDict_uniq_entries"));

        System.out.println("Create TransDict Dic...");
        TripletDic transDict= new TripletDic();
        BufferedReader reader= new BufferedReader(new FileReader(transDictPath));
        String line2read="";
        int counter=0;

        while ((line2read= reader.readLine())!= null)
        {
            counter++;
            if (counter%10000==0)
                System.out.println(counter);
            //EGY/POS/EN/MSA
            line2read= line2read.trim();
            String[] splitLine= line2read.split("\t");
            String EGY= Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(splitLine[0]));
            String MSA= Preprocessing.normalizeAlefYa(Preprocessing.undiacratize(splitLine[3]));
            String En= splitLine[2];
            transDict.update_all_dics(En, MSA, EGY);
            transDict_uniq_entries.add(En+"\t"+MSA+"\t"+EGY);

        }

        System.out.println("Number of Entries (En) in TransDict: "+ transDict.getEn_MSA_EGY_Map().size());
        System.out.println("Number of unique entries in TransDict "+ transDict_uniq_entries.size());

        for (String triplet: transDict_uniq_entries)
        transDict_uniq_entries_writer.write(triplet+"\n");

        transDict_uniq_entries_writer.flush();
        transDict_uniq_entries_writer.close();

        return transDict;
    }

}
