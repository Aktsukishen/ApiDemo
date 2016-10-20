package com.lishensong.apidemo.view;

import android.app.ExpandableListActivity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bestv on 2016/10/20.
 */

public class ExpandableListActivities {

    public static class ExpandableList1 extends ExpandableListActivity{
        ExpandableListAdapter mAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mAdapter = new MyExpandableListAdapter();
            setListAdapter(mAdapter);
            registerForContextMenu(getExpandableListView());

            getExpandableListView().setOnChildClickListener(childClickListener);
            getExpandableListView().setOnGroupClickListener(groupClickListener);
            getExpandableListView().setOnGroupExpandListener(expandListener);
            getExpandableListView().setOnGroupCollapseListener(collapseListener);
        }

        private ExpandableListView.OnChildClickListener childClickListener = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(ExpandableList1.this,"Click Child " + childPosition + " clicked in group " + groupPosition,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        private ExpandableListView.OnGroupClickListener groupClickListener = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Toast.makeText(ExpandableList1.this, "Click Group " + groupPosition, Toast.LENGTH_SHORT).show();
                return false;
            }
        };

        private ExpandableListView.OnGroupExpandListener expandListener = new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(ExpandableList1.this, "Expand Group " + groupPosition, Toast.LENGTH_SHORT).show();
            }
        };

        private ExpandableListView.OnGroupCollapseListener collapseListener = new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(ExpandableList1.this, "Collapsed Group " + groupPosition, Toast.LENGTH_SHORT).show();
            }
        };

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Sample menu");
            menu.add(0,0,0,"Sample Action");
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {
            ExpandableListView.ExpandableListContextMenuInfo info =
                    (ExpandableListView.ExpandableListContextMenuInfo)item.getMenuInfo();
            String title = ((TextView)info.targetView).getText().toString();
            int type = ExpandableListView.getPackedPositionType(info.packedPosition);
            if(type == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition);
                int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition);
                Toast.makeText(this,title + ":Child " + childPos + " clicked in group " + groupPos,
                        Toast.LENGTH_SHORT).show();
                return true;
            }else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                int groupPos  = ExpandableListView.getPackedPositionGroup(info.packedPosition);
                Toast.makeText(this, title + ": Group " + groupPos + " clicked",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }

        private class MyExpandableListAdapter extends BaseExpandableListAdapter{
            private String[] groups = {"水浒传","西游记","红楼梦","三国演义"};
            private String[][] children = {
                    {"宋江","李逵","鲁智深"},
                    {"师傅","大师兄","二师兄","沙僧"},
                    {"贾宝玉","林黛玉","王熙凤"},
                    {"刘备","关羽","张飞"}
            };

            @Override
            public int getGroupCount() {
                return groups.length;
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return children[groupPosition].length;
            }

            @Override
            public Object getGroup(int groupPosition) {
                return groups[groupPosition];
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return children[groupPosition][childPosition];
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                TextView textView = geGenericView();
                textView.setText(getGroup(groupPosition).toString());
                return textView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                TextView textView = geGenericView();
                textView.setText((getChild(groupPosition,childPosition).toString()));
                return textView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }

            private TextView geGenericView(){
                AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
                                                ViewGroup.LayoutParams.MATCH_PARENT,64);
                TextView textView = new TextView(ExpandableList1.this);
                textView.setLayoutParams(lp);
                textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.LEFT);
                textView.setPaddingRelative(36,0,0,0);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                return textView;
            }


        }
    }

    public static class ExpandableList2 extends  ExpandableListActivity{

        private static final String[] CONTACTS_PROJECTION = new String[]{
                Contacts._ID,
                Contacts.DISPLAY_NAME
        };
        private static final int GROUP_ID_COLUMN_INDEX = 0;

        private static final String[] PHONE_NUMBER_PROJECTION = new String[]{
                Phone._ID,
                Phone.NUMBER
        };

        private static final int TOKEN_GROUP = 0;
        private static final int TOKEN_CHILD = 1;

        private QueryHandler mQueryHandler;
        private CursorTreeAdapter mAdapter;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mAdapter = new MyExpandableListAdapter(this,
                    android.R.layout.simple_expandable_list_item_1,
                    android.R.layout.simple_expandable_list_item_2,
                    new String[]{Contacts.DISPLAY_NAME},
                    new int[]{android.R.id.text1},
                    new String[]{Phone.NUMBER},
                    new int[]{android.R.id.text1}
                    );
            setListAdapter(mAdapter);

            mQueryHandler = new QueryHandler(this,mAdapter);
            //Query for People
            mQueryHandler.startQuery(TOKEN_GROUP,null,Contacts.CONTENT_URI,CONTACTS_PROJECTION,
                    Contacts.HAS_PHONE_NUMBER + "=1",null,null);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            mAdapter.changeCursor(null);
            mAdapter = null;
        }

        private static  class QueryHandler extends AsyncQueryHandler{

            private CursorTreeAdapter mAdapter;

            public QueryHandler(Context context,CursorTreeAdapter adapter){
                super(context.getContentResolver());
                this.mAdapter = adapter;
            }

            @Override
            protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                switch (token){
                    case TOKEN_GROUP:
                        mAdapter.setGroupCursor(cursor);
                        break;
                    case TOKEN_CHILD:
                        int groupPosition = (Integer) cookie;
                        mAdapter.setChildrenCursor(groupPosition,cursor);
                        break;
                }
            }
        }

        private class MyExpandableListAdapter extends SimpleCursorTreeAdapter{

            public MyExpandableListAdapter(Context context,int groupLayout,int childLayout,
                                           String[] groupFrom,int[] groupTo,
                                           String[] childrenFrom,int[] childrenTo){
                super(context,null,groupLayout,childLayout,groupFrom,groupTo,childLayout,childrenFrom,childrenTo);
            }

            @Override
            protected Cursor getChildrenCursor(Cursor groupCursor) {
                //Given the group, we return a cursor for all the children within that group

                //Return a cursor that points to this contact's phone numbers
                Uri.Builder builder = Contacts.CONTENT_URI.buildUpon();
                ContentUris.appendId(builder,groupCursor.getLong(GROUP_ID_COLUMN_INDEX));
                builder.appendEncodedPath(Contacts.Data.CONTENT_DIRECTORY);
                Uri phoneNumbersUri = builder.build();
                mQueryHandler.startQuery(TOKEN_CHILD,groupCursor.getPosition(),phoneNumbersUri,
                        PHONE_NUMBER_PROJECTION,Phone.MIMETYPE + " = ?",new String[]{Phone.CONTENT_ITEM_TYPE}
                        ,null
                        );
                return null;
            }
        }

    }

    public static class ExpandableList3 extends ExpandableListActivity{
        private static final String NAME = "NAME";
        private static final String IS_EVEN = "IS_EVEN";
        private ExpandableListAdapter mAdapter;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            List<Map<String,String>> groupData = new ArrayList<Map<String,String>>();
            List<List<Map<String,String>>> childData = new ArrayList<List<Map<String,String>>>();
            for(int i = 0; i< 20; i++){
                Map<String,String> curGroupMap =new HashMap<>();
                groupData.add(curGroupMap);
                curGroupMap.put(NAME,"Group " + i);
                curGroupMap.put(IS_EVEN,(i%2 == 0) ? "This group is even " : "This group is odd");

                List<Map<String,String>> children = new ArrayList<>();
                for(int j = 0; j < 15 ;j++){
                    Map<String,String> curChildMap = new HashMap<>();
                    children.add(curChildMap);
                    curChildMap.put(NAME,"Group " + i);
                    curChildMap.put(IS_EVEN,(i%2 == 0) ? "This group is even " : "This group is odd");
                }
                childData.add(children);
            }
            mAdapter = new SimpleExpandableListAdapter(this,
                    groupData,
                    android.R.layout.simple_expandable_list_item_1,
                    new String[]{NAME,IS_EVEN},
                    new int[]{android.R.id.text1,android.R.id.text2},
                    childData,
                    android.R.layout.simple_expandable_list_item_2,
                    new String[]{NAME,IS_EVEN},
                    new int[]{android.R.id.text1,android.R.id.text2}
                    );
            setListAdapter(mAdapter);
        }
    }
}
