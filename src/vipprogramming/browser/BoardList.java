package vipprogramming.browser;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

public class BoardList {

	public MyParserCallback cb = new MyParserCallback();
	public BoardList(){
		URL url = null;
		HttpURLConnection http_url_connection = null;
		int response_code;
		String response_message;
		try {
			url = new URL("http://menu.2ch.net/bbsmenu.html");
			http_url_connection = (HttpURLConnection) url.openConnection();
			http_url_connection.setRequestMethod("GET");
			http_url_connection.setInstanceFollowRedirects(false);
			http_url_connection.setRequestProperty("User-Agent", "Monazilla/1.00");
		} catch (ProtocolException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			response_code = http_url_connection.getResponseCode();
			response_message = http_url_connection.getResponseMessage();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InputStreamReader in = null;
		try {
			in = new InputStreamReader(http_url_connection.getInputStream(), "SJIS");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(in);
		ParserDelegator pd = new ParserDelegator();

		try {
			pd.parse(reader, cb, true);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		http_url_connection.disconnect();

		for (Map.Entry<String, String> e : cb.getBoard_map().entrySet()) {
			if (!e.getValue().equals(""))
				System.out.print(e.getValue() +"  ");
			System.out.println(e.getKey());
		}
	}
	public Map<String,String> getBoard_list(){
		return cb.getBoard_map();
	}
}

class MyParserCallback extends HTMLEditorKit.ParserCallback {
	boolean start_bold = false;
	boolean parse_2ch_start_flag = false;
	boolean start_category_flag = false;
	String start_trigger_text = "2ch総合案内";
	ArrayList<String> ignore_text = new ArrayList<String>();
	ArrayList<String> ignore_category = new ArrayList<String>();
	String board_category_name = "";
	String next_board_url = "";
	LinkedHashMap<String, String> board_map = new LinkedHashMap<String, String>();

	public MyParserCallback() {
		ignore_text.add("2ch検索");
		ignore_text.add("be.2ch.net");
		ignore_text.add("アンケート");
		ignore_text.add("2chビューア");
		ignore_text.add("2chオークション");
		ignore_text.add("2ch観察帳");
		ignore_text.add("2chメルマガ");

		ignore_category.add("おすすめ");
		ignore_category.add("チャット");
		ignore_category.add("２ｃｈ＠ＩＲＣ");
		ignore_category.add("運営案内");
		ignore_category.add("ガイドライン");
		ignore_category.add("2chメルマガ");
		ignore_category.add("ツール類");
		ignore_category.add("BBSPINK");
		ignore_category.add("まちＢＢＳ");
		ignore_category.add("他のサイト");
	}

	@Override
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attr, int pos) {
		if (tag.equals(HTML.Tag.B)) {
			start_bold = true;
			start_category_flag = true;
		}
		if (tag.equals(HTML.Tag.A)) {
			if (parse_2ch_start_flag == true) {
				String href = (String) attr.getAttribute(HTML.Attribute.HREF);
				next_board_url = href;
			}
		}
	}

	@Override
	public void handleEndTag(HTML.Tag tag, int pos) {
		if (tag.equals(HTML.Tag.B)) {
			start_bold = false;
		}
	}

	public void handleText(char[] data, int pos) {
		String text = new String(data);
		if (start_bold) {
			for (String s : ignore_category) {
				if (text.equals(s)) {
					start_category_flag = false;
					continue;
				}
			}
			if (start_category_flag) {
				board_category_name = text;
				board_map.put(text,"c");
			}
		} else if (text.equals(start_trigger_text)) {
			parse_2ch_start_flag = true;
		} else if (parse_2ch_start_flag && start_category_flag) {
			Pattern pattern = Pattern.compile(".*\\.2ch\\.net/.*");
			Matcher matcher = pattern.matcher(next_board_url);
			boolean blnMatch = matcher.matches();
			for (String s : ignore_text) {
				if (text.equals(s)) {
					blnMatch = false;
					continue;
				}
			}
			if (blnMatch) {
				board_map.put(text,next_board_url);
			}
			next_board_url = "";

		}
	}

	public Map<String, String> getBoard_map() {
		return board_map;
	}
}