package GoldDataGenerationUsingBabelNet;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by monadiab on 5/3/16.
 */
public class TripletDic {
    HashSet<String> EGY_words;
    HashMap<String, HashSet<String>> En_EGY_Map;
    HashMap<String, HashSet<String>> MSA_EGY_Map;
    HashMap<String, HashMap<String, HashSet<String>>> En_MSA_EGY_Map;

    public TripletDic()
    {
        EGY_words= new HashSet<String>();
        En_EGY_Map= new HashMap<String, HashSet<String>>();
        MSA_EGY_Map= new HashMap<String, HashSet<String>>();
        En_MSA_EGY_Map= new HashMap<String, HashMap<String, HashSet<String>>>();
    }

    public HashMap<String, HashMap<String, HashSet<String>>> getEn_MSA_EGY_Map() {return En_MSA_EGY_Map;}

    public HashMap<String, HashSet<String>> getMSA_EGY_Map() {return MSA_EGY_Map;}

    public HashMap<String, HashSet<String>> getEn_EGY_Map() {return En_EGY_Map;}

    public HashSet<String> getEGY_words() {return EGY_words;}

    public void update_all_dics(String En, String MSA, String EGY)
    {
        //update En_MSA_EGY_Map
        if (!En_MSA_EGY_Map.containsKey(En)) {
            HashSet<String> EGYs= new HashSet<String>();
            EGYs.add(EGY);
            HashMap<String, HashSet<String>> MSA_EGY= new HashMap<String, HashSet<String>>();
            MSA_EGY.put(MSA, EGYs);
            En_MSA_EGY_Map.put(En, MSA_EGY );

        }else if (!En_MSA_EGY_Map.get(En).containsKey(MSA)) {
            HashSet<String> EGYs= new HashSet<String>();
            EGYs.add(EGY);
            En_MSA_EGY_Map.get(En).put(MSA, EGYs);
        }
        else if (!En_MSA_EGY_Map.get(En).get(MSA).contains(EGY))
            En_MSA_EGY_Map.get(En).get(MSA).add(EGY);

        //update MSA_EGY_Map
        if (!MSA_EGY_Map.containsKey(MSA))
        {
            HashSet<String> EGYs= new HashSet<String>();
            EGYs.add(EGY);
            MSA_EGY_Map.put(MSA, EGYs);
        }else if (!MSA_EGY_Map.get(MSA).contains(EGY))
            MSA_EGY_Map.get(MSA).add(EGY);

        //update En_EGY_Map
        if (!En_EGY_Map.containsKey(En))
        {
            HashSet<String> EGYs= new HashSet<String>();
            EGYs.add(EGY);
            En_EGY_Map.put(En, EGYs);
        }else if (!En_EGY_Map.get(En).contains(EGY))
            En_EGY_Map.get(En).add(EGY);

        //update EGY_words
        if (!EGY_words.contains(EGY))
            EGY_words.add(EGY);
    }

    public void update (String En, String MSA, String EGY)
    {
        //update En_MSA_EGY_Map
        if (!En_MSA_EGY_Map.containsKey(En)) {
            HashSet<String> EGYs= new HashSet<String>();
            EGYs.add(EGY);
            HashMap<String, HashSet<String>> MSA_EGY= new HashMap<String, HashSet<String>>();
            MSA_EGY.put(MSA, EGYs);
            En_MSA_EGY_Map.put(En, MSA_EGY );

        }else if (!En_MSA_EGY_Map.get(En).containsKey(MSA)) {
            HashSet<String> EGYs= new HashSet<String>();
            EGYs.add(EGY);
            En_MSA_EGY_Map.get(En).put(MSA, EGYs);
        }
        else if (!En_MSA_EGY_Map.get(En).get(MSA).contains(EGY))
            En_MSA_EGY_Map.get(En).get(MSA).add(EGY);
    }

    public void compare(TripletDic t, String common_triplets,String suffix) throws IOException
    {
        System.out.println("Comparing BabelNetDic and TransDic...");

        BufferedWriter common_triplets_writer= new BufferedWriter(new FileWriter(common_triplets));
        BufferedWriter En_sim_MSA_sim_EGY_diff_writer= new BufferedWriter(new FileWriter("En_sim_MSA_sim_EGY_diff_"+suffix));
        BufferedWriter En_sim_MSA_diff_EGY_diff_writer= new BufferedWriter(new FileWriter("En_sim_MSA_diff_EGY_diff_"+suffix));
        BufferedWriter En_sim_MSA_diff_EGY_sim_writer= new BufferedWriter(new FileWriter("En_sim_MSA_diff_EGY_sim_"+suffix));

        BufferedWriter En_diff_MSA_sim_EGY_diff_writer= new BufferedWriter(new FileWriter("En_diff_MSA_sim_EGY_diff_"+suffix));
        BufferedWriter En_diff_MSA_sim_EGY_sim_writer= new BufferedWriter(new FileWriter("En_diff_MSA_sim_EGY_sim_"+suffix));
        BufferedWriter En_diff_MSA_diff_EGY_diff_writer= new BufferedWriter(new FileWriter("En_diff_MSA_diff_EGY_diff_"+suffix));
        BufferedWriter En_diff_MSA_diff_EGY_sim_writer= new BufferedWriter(new FileWriter("En_diff_MSA_diff_EGY_sim_"+suffix));


        HashMap<String, HashMap<String, HashSet<String>>> t_En_MSA_EGY_Map= t.getEn_MSA_EGY_Map();
        int counter=0;

        for (String t_En: t_En_MSA_EGY_Map.keySet())
        {
            counter++;
            if (counter%10000==0)
                System.out.println(counter);

            if (En_MSA_EGY_Map.containsKey(t_En))
            {
                Set<String> t_MSA= t_En_MSA_EGY_Map.get(t_En).keySet();
                Set<String> b_MSA= En_MSA_EGY_Map.get(t_En).keySet();

                HashSet<String> common_MSA= new HashSet<String>(t_MSA);
                HashSet<String> diff_MSA= new HashSet<String>(t_MSA);
                common_MSA.retainAll(b_MSA);
                diff_MSA.removeAll(common_MSA);

                for (String t_b_MSA: common_MSA)
                {
                    HashSet<String> t_EGY= t_En_MSA_EGY_Map.get(t_En).get(t_b_MSA);
                    HashSet<String> b_EGY= En_MSA_EGY_Map.get(t_En).get(t_b_MSA);

                    HashSet<String> common_EGY= new HashSet<String>(t_EGY);
                    HashSet<String> diff_EGY= new HashSet<String>(t_EGY);
                    common_EGY.retainAll(b_EGY);
                    diff_EGY.removeAll(common_EGY);

                    for (String t_b_EGY: common_EGY)
                        common_triplets_writer.write(t_En+"\t"+t_b_MSA+"\t"+t_b_EGY+"\n");
                    for (String t_only_EGY: diff_EGY)
                        En_sim_MSA_sim_EGY_diff_writer.write(t_En+"\t"+t_b_MSA+"\t"+t_only_EGY+"\n");
                }

                for (String t_only_MSA: diff_MSA)
                {
                    HashSet<String> t_EGY= t_En_MSA_EGY_Map.get(t_En).get(t_only_MSA);
                    HashSet<String> b_EGY= En_EGY_Map.get(t_En);

                    HashSet<String> common_EGY= new HashSet<String>(t_EGY);
                    HashSet<String> diff_EGY= new HashSet<String>(t_EGY);
                    common_EGY.retainAll(b_EGY);
                    diff_EGY.removeAll(common_EGY);

                    for (String t_b_EGY: common_EGY)
                        En_sim_MSA_diff_EGY_sim_writer.write(t_En+"\t"+t_only_MSA+"\t"+t_b_EGY+"\n");
                    for (String t_only_EGY: diff_EGY)
                        En_sim_MSA_diff_EGY_diff_writer.write(t_En+"\t"+t_only_MSA+"\t"+t_only_EGY+"\n");
                }

            }else
            {
                //t_En is not found in BabelNet
                Set<String> t_MSA_set= t_En_MSA_EGY_Map.get(t_En).keySet();
                for (String t_MSA: t_MSA_set)
                {
                    if (MSA_EGY_Map.containsKey(t_MSA))
                    {
                        HashSet<String> t_EGY= t_En_MSA_EGY_Map.get(t_En).get(t_MSA);
                        HashSet<String> b_EGY= MSA_EGY_Map.get(t_MSA);

                        HashSet<String> common_EGY= new HashSet<String>(t_EGY);
                        HashSet<String> diff_EGY= new HashSet<String>(t_EGY);
                        common_EGY.retainAll(b_EGY);
                        diff_EGY.removeAll(common_EGY);

                        for (String t_b_EGY: common_EGY)
                            En_diff_MSA_sim_EGY_sim_writer.write(t_En+"\t"+t_MSA+"\t"+t_b_EGY+"\n");
                        for (String t_only_EGY: diff_EGY)
                            En_diff_MSA_sim_EGY_diff_writer.write(t_En+"\t"+t_MSA+"\t"+t_only_EGY+"\n");
                    }
                    else
                    {
                        HashSet<String> t_EGY_set= t_En_MSA_EGY_Map.get(t_En).get(t_MSA);
                        for (String t_EGY: t_EGY_set)
                        {
                            if (EGY_words.contains(t_EGY))
                                En_diff_MSA_diff_EGY_sim_writer.write(t_En+"\t"+t_MSA+"\t"+t_EGY+"\n");
                            else
                                En_diff_MSA_diff_EGY_diff_writer.write(t_En+"\t"+t_MSA+"\t"+t_EGY+"\n");
                        }
                    }
                }
            }
        }

        common_triplets_writer.flush();
        common_triplets_writer.close();

        En_sim_MSA_sim_EGY_diff_writer.flush();
        En_sim_MSA_sim_EGY_diff_writer.close();

        En_sim_MSA_diff_EGY_diff_writer.flush();
        En_sim_MSA_diff_EGY_diff_writer.close();

        En_sim_MSA_diff_EGY_sim_writer.flush();
        En_sim_MSA_diff_EGY_sim_writer.close();

        En_diff_MSA_sim_EGY_diff_writer.flush();
        En_diff_MSA_sim_EGY_diff_writer.close();

        En_diff_MSA_sim_EGY_sim_writer.flush();
        En_diff_MSA_sim_EGY_sim_writer.close();

        En_diff_MSA_diff_EGY_diff_writer.flush();
        En_diff_MSA_diff_EGY_diff_writer.close();

        En_diff_MSA_diff_EGY_sim_writer.flush();
        En_diff_MSA_diff_EGY_sim_writer.close();

    }

}
