package vipprogramming.browser;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PrefFileIO {
	public static Map<String, String> ReadBoardList() {
		Map<String, String> bbs_map = new LinkedHashMap<String,String>();
		try{
            File file = new File("bbstree.txt");
            BufferedReader b = new BufferedReader(new FileReader(file));
            String s;
            while((s = b.readLine())!=null){
            	String[] sAry = s.split(",");
            	bbs_map.put(sAry[0], sAry[1]);
            }
        }catch(Exception e){
            System.out.println("ファイル読み込み失敗");
        }
		return bbs_map;
	}
	public static void writeBoardList(Map<String, String> bl) {
        try{
            File file = new File(".settings/bbstree.txt");
                PrintWriter pw
                  = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        		for(Entry<String, String> entry : bl.entrySet()){
        			if(entry.getValue().equals("c")){
                        pw.println(entry.getKey()+",c");
        			}else{
                        pw.println(entry.getKey()+"," + entry.getValue());
        			}
        		}
        		pw.close();
        }catch(IOException e){
            System.out.println(e);
        }
	}
}
