
package sleek.ChatWindow;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;

import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import sleek.SocketChat.R;

public class ChatMsgViewAdapter extends BaseAdapter {
    private static final String TAG = ChatMsgViewAdapter.class.getSimpleName();

    private ArrayList<ChatMsgEntity> coll;

    private Context ctx;
    private int dmw;
    public ChatMsgViewAdapter(Context context, ArrayList<ChatMsgEntity> coll, int dmw) {
        ctx = context;
        this.coll = coll;
        this.dmw = dmw;
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int arg0) {
        return false;
    }

    public int getCount() {
        return coll.size();
    }

    public Object getItem(int position) {
        return coll.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMsgEntity entity = coll.get(position);
        int itemLayout = entity.getLayoutID();

        LinearLayout layout = new LinearLayout(ctx);
        LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        vi.inflate(itemLayout, layout, true);

        TextView tvName = (TextView) layout.findViewById(R.id.messagedetail_row_name);
        tvName.setText(entity.getName());
        tvName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        TextPaint paintname = tvName.getPaint(); 
        int lename = (int) paintname.measureText(entity.getName()); 

        TextView tvText = (TextView) layout.findViewById(R.id.messagedetail_row_text);
        tvText.setText(entity.getText());
        TextPaint paint = tvText.getPaint(); 
        int len = (int) paint.measureText(entity.getText()); 
        
        TextView tvDate = (TextView) layout.findViewById(R.id.messagedetail_row_date);
        tvDate.setText(entity.getDate());
        tvDate.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        TextPaint paintdate = tvDate.getPaint(); 
        int lendate = (int) paintdate.measureText(entity.getDate()); 
        
        if(len>dmw){
        	System.out.println("dmw:"+dmw+"  len:"+len +" lename:"+lename+" lendate:"+lendate);
        	int lendateint = (int) (lendate*1.8);
        	tvDate.setPadding(dmw-(lename+lendateint-2), 0, 0, 0);
        }else{
        	tvDate.setPadding(len, 0, 0, 0);
        }
        
        return layout;
    }

    public int getViewTypeCount() {
        return coll.size();
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
}
