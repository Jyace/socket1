package sleek.SocketChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Friends{
	
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private Map<String, Object> map;
	public Friends(String[] all,String name,String me){
		this.addFriends(all,name,me);
	}
	
	public void setName(String[] all,String name,String me){
		this.addFriends(all,name,me);
	}
	
	public List<Map<String, Object>> getFriends(){
		return list;
	}
	
	public List<Map<String, Object>> addFriends(String[] allf,String getf,String me){
		String[] str;
		System.out.println("获取所有在线用户："+getf);
		if(getf!=null){
			getf=getf.substring(config.PROTOCOL_FRIENDS_START.length(), getf.length()-config.PROTOCOL_FRIENDS_END.length());
			str = getf.split(config.PROTOCOL_FRIENDS_SEPARATE);
			if(allf!=null){
			for (int i = 0; i < allf.length; i++) {
				if(!allf[i].equals(me)){
					
					int count=0;
					if(str!=null){
						for(int j=0;j<str.length;j++)
						{
							System.out.println("所有好友allf["+i+"]"+allf[i]+"   在线好友："+"str["+j+"]"+str[j]);
							if(str[j]!=null){
								if(str[j].equals(allf[i])){
							        count++;
							    }
							}
						}
					}
					map = new HashMap<String, Object>();
					map.put("friendname", allf[i]);
					if(count==0){
						map.put("ofline", "(offline)");
						map.put("avatar", R.drawable.icon_gray);
					}else{
						map.put("ofline", "(online)");
						map.put("avatar", R.drawable.icon);
					}
					list.add(map);
				}
			}
			}
		}else{
			if(allf!=null){
			for (int i = 0; i < allf.length; i++) {
				if(!allf[i].equals(me)){
					map = new HashMap<String, Object>();
					map.put("friendname", allf[i]);
					map.put("ofline", "(offline)");
					map.put("avatar", R.drawable.icon_gray);
					list.add(map);
				}
			}
			}
		}
		return list;
	}
}