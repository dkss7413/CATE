package app.com.CATE.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Objects;

import app.com.CATE.models.CategoryModel;
import app.com.youtubeapiv3.R;


public class CategoryAdapter extends BaseAdapter {
    // Adapter 에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<CategoryModel> listViewItemList = new ArrayList<>() ;

    // ListViewAdapter 의 생성자
    public CategoryAdapter() {

    }

    // Adapter 에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position 에 위치한 데이터를 화면에 출력하는데 사용될 View 를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listView_item" Layout 을 inflate 하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.category_list_layout, parent, false);
        }

        // 화면에 표시될 View(Layout 이 inflate 된)으로부터 위젯에 대한 참조 획득
        LinearLayout cate_list = convertView.findViewById(R.id.list_category);
        ImageView cate_id = convertView.findViewById(R.id.imageCategoryIcon);
        TextView cate_name = convertView.findViewById(R.id.text_name);

        // Data Set(listViewItemList)에서 position 에 위치한 데이터 참조 획득
        CategoryModel listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        switch (listViewItem.getName()) {
            case "영화":
                cate_id.setImageResource(R.drawable.icon_movie);
                break;
            case "게임":
                cate_id.setImageResource(R.drawable.icon_game);
                break;
            case "스포츠":
                cate_id.setImageResource(R.drawable.icon_sport);
                break;
            case "음악":
                cate_id.setImageResource(R.drawable.icon_music);
                break;
            case "롤":
                cate_id.setImageResource(R.drawable.icon_lol);
                break;
            case "재즈":
                cate_id.setImageResource(R.drawable.icon_jazz);
                break;
            case "동물":
                cate_id.setImageResource(R.drawable.icon_animal);
                break;
            case "배그":
                cate_id.setImageResource(R.drawable.icon_battleground);
                break;
            case "유머":
                cate_id.setImageResource(R.drawable.icon_humor);
                break;


            default:
                cate_id.setImageResource(R.drawable.ic_baseline_clear_24px);
                break;
        }

        cate_name.setText(listViewItem.getName());

        if(listViewItem.getState()) {
            cate_list.setBackgroundColor(Color.LTGRAY);
        }

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String id, String title, String desc, String channelId, Boolean state) {
        CategoryModel item = new CategoryModel(id, title, desc, channelId, state);

        item.setId(id);
        item.setName(title);
        item.setDetail(desc);
        item.setKey(channelId);
        listViewItemList.add(item);
    }
}
