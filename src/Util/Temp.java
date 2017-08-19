package Util;

public class Temp {
	public static void main(String[] args) {
		String[] str=FileUtil.getFileContext("fh.txt").split("\n");
		for(String line:str){
			String[] m=line.split(",");
			String result="<div class=\"zhongjiang\"><table class=\"zhongjiangtable\"><tr><td colspan=\"2\"><a href=\""+m[0]+"\" ><img class=\"img30a\"src=\""+m[1]+"\"width=\"251px\" height=\"251px\"></a></td></tr></table></div>";
			FileUtil.println(result,"re.txt");
		}
	}
}
