package com.lishensong.apidemo.view;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.TextView;

import com.lishensong.apidemo.R;

import java.util.List;

/**
 * Created by li.shensong on 2016/10/13.
 */
public class ListActivities {

    public static class List1 extends  ListActivity{
        private String[] mStrings = Cheeses.sCheeseStrings;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setListAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,
                    mStrings));
            getListView().setTextFilterEnabled(true);
        }
    }

    public static class List2 extends ListActivity{

        private static final String[] CONTACT_PROJECTION = new String[]{
                Contacts._ID, Contacts.DISPLAY_NAME};
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Cursor c = getContentResolver().query(Contacts.CONTENT_URI,
                    CONTACT_PROJECTION,null,null,null);
            startManagingCursor(c);
            ListAdapter adapter = new SimpleCursorAdapter(this,
                    //Use a template that displays a text view
                    android.R.layout.simple_list_item_1,
                    //Give the cursor to the list adapter
                    c,
                    //Map the Name column int the people database to
                    new String[]{Contacts.DISPLAY_NAME},
                    //the "text1" view defined in the XML template
                    new int[]{android.R.id.text1}
                    );
            setListAdapter(adapter);
        }
    }

    public static class List3 extends  ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

        private SimpleCursorAdapter mAdapter;
        private String[] PROJECTION = new String[]{Contacts._ID,Contacts.DISPLAY_NAME};
        static final String SELECTION = "((" +
                Contacts.DISPLAY_NAME + " NOTNULL) AND (" +
                Contacts.DISPLAY_NAME + " != '' ))";


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ProgressBar progressBar = new ProgressBar(this);
            progressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.CENTER));
            progressBar.setIndeterminate(true);
            getListView().setEmptyView(progressBar);

            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            root.addView(progressBar);

            String[] fromColumns = new String[]{Contacts.DISPLAY_NAME};
            int[] toViews =new int[]{android.R.id.text1};
            mAdapter  = new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,
                    null,fromColumns,toViews,0);
            getListView().setAdapter(mAdapter);

            getLoaderManager().initLoader(0,null,this);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(this,Contacts.CONTENT_URI,PROJECTION,null,null,null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
    }

    public static class List4 extends ListActivity{
        private String[] PHONE_PROJECTION = new String[]{Phone._ID,Phone.TYPE,Phone.LABEL,Phone.NUMBER};
        private static final int COLUMN_TYPE = 1;
        private static final int COLUMN_LABEL = 2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Cursor c = getContentResolver().query(Phone.CONTENT_URI,PHONE_PROJECTION,null,null,null);
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,c,
                    new String[]{
                            Phone.TYPE,
                            Phone.NUMBER
                    },
                    new int[]{android.R.id.text1,android.R.id.text2}
                    );
            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    if(columnIndex != COLUMN_TYPE){
                        return false;
                    }
                    int type = cursor.getInt(COLUMN_TYPE);
                    String label = null;
                    if( type == Phone.TYPE_CUSTOM){
                        label = cursor.getString(COLUMN_LABEL);
                    }
                    String text = (String) Phone.getTypeLabel(getResources(),type,label);
                    ((TextView) view).setText(text);
                    return true;
                }
            });
            setListAdapter(adapter);
        }
    }

    public static class List5 extends ListActivity{

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setListAdapter(new SpeechListAdapter(this));
        }

        private class SpeechListAdapter extends BaseAdapter{
            private Context mContext;

            public SpeechListAdapter(Context context) {
                mContext = context;
            }

            @Override
            public int getCount() {
                return Shakespeare.TITLES.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                SpeechView sv;
                if(convertView == null){
                    sv  = new SpeechView(mContext,Shakespeare.TITLES[position],
                            Shakespeare.DIALOGUE[position]
                            );
                }else{
                    sv = (SpeechView) convertView;
                    sv.setTitle(Shakespeare.TITLES[position]);
                    sv.setDialogue(Shakespeare.DIALOGUE[position]);
                }
                return sv;
            }
        }

        private class SpeechView extends LinearLayout{
            private TextView mTitle;
            private TextView mDialogue;

            public SpeechView(Context context, String title, String words) {
                super(context);
                this.setOrientation(VERTICAL);

                mTitle = new TextView(context);
                mTitle.setText(title);
                addView(mTitle,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
                mDialogue = new TextView(context);
                mDialogue.setText(words);
                addView(mDialogue,new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
            }

            public void setTitle(String title){
                mTitle.setText(title);
            }

            public void setDialogue(String words){
                mDialogue.setText(words);
            }
        }
    }

    public static class List6 extends  ListActivity{

        private String[] mStrings = {
                "----------",
                "----------",
                "Abbaye de Belloc",
                "Abbaye du Mont des Cats",
                "Abertam",
                "----------",
                "Abondance",
                "----------",
                "Ackawi",
                "Acorn",
                "Adelost",
                "Affidelice au Chablis",
                "Afuega'l Pitu",
                "Airag",
                "----------",
                "Airedale",
                "Aisy Cendre",
                "----------",
                "Allgauer Emmentaler",
                "Alverca",
                "Ambert",
                "American Cheese",
                "Ami du Chambertin",
                "----------",
                "----------",
                "Anejo Enchilado",
                "Anneau du Vic-Bilh",
                "Anthoriro",
                "----------",
                "----------"
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setListAdapter(new MyListAdapter(this));
        }

        private class MyListAdapter extends  BaseAdapter{
            private Context mContext ;

            public MyListAdapter(Context context){
                mContext = context;
            }

            @Override
            public int getCount() {
                return mStrings.length;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return !mStrings[position].startsWith("-");
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView tv;
                if(convertView == null){
                    tv = (TextView) LayoutInflater.from(mContext).inflate(
                            android.R.layout.simple_expandable_list_item_1,parent,false
                    );
                }else{
                    tv = (TextView) convertView;
                }
                tv.setText(mStrings[position]);
                return tv;
            }
        }
    }

    public static class List7 extends  ListActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setListAdapter(new SpeechListAdpater(this) );
        }

        @Override
        protected void onListItemClick(ListView l, View v, int position, long id) {
            ((SpeechListAdpater)getListAdapter()).toggle(position);
        }

        private class SpeechListAdpater extends  BaseAdapter{
            private Context mContext;

            public SpeechListAdpater(Context context){
                mContext = context;
            }

            private String[] mTitles =
                    {
                            "Henry IV (1)",
                            "Henry V",
                            "Henry VIII",
                            "Richard II",
                            "Richard III",
                            "Merchant of Venice",
                            "Othello",
                            "King Lear"
                    };

            /**
             * Our data, part 2.
             */
            private String[] mDialogue =
                    {
                            "So shaken as we are, so wan with care," +
                                    "Find we a time for frighted peace to pant," +
                                    "And breathe short-winded accents of new broils" +
                                    "To be commenced in strands afar remote." +
                                    "No more the thirsty entrance of this soil" +
                                    "Shall daub her lips with her own children's blood;" +
                                    "Nor more shall trenching war channel her fields," +
                                    "Nor bruise her flowerets with the armed hoofs" +
                                    "Of hostile paces: those opposed eyes," +
                                    "Which, like the meteors of a troubled heaven," +
                                    "All of one nature, of one substance bred," +
                                    "Did lately meet in the intestine shock" +
                                    "And furious close of civil butchery" +
                                    "Shall now, in mutual well-beseeming ranks," +
                                    "March all one way and be no more opposed" +
                                    "Against acquaintance, kindred and allies:" +
                                    "The edge of war, like an ill-sheathed knife," +
                                    "No more shall cut his master. Therefore, friends," +
                                    "As far as to the sepulchre of Christ," +
                                    "Whose soldier now, under whose blessed cross" +
                                    "We are impressed and engaged to fight," +
                                    "Forthwith a power of English shall we levy;" +
                                    "Whose arms were moulded in their mothers' womb" +
                                    "To chase these pagans in those holy fields" +
                                    "Over whose acres walk'd those blessed feet" +
                                    "Which fourteen hundred years ago were nail'd" +
                                    "For our advantage on the bitter cross." +
                                    "But this our purpose now is twelve month old," +
                                    "And bootless 'tis to tell you we will go:" +
                                    "Therefore we meet not now. Then let me hear" +
                                    "Of you, my gentle cousin Westmoreland," +
                                    "What yesternight our council did decree" +
                                    "In forwarding this dear expedience.",

                            "Hear him but reason in divinity," +
                                    "And all-admiring with an inward wish" +
                                    "You would desire the king were made a prelate:" +
                                    "Hear him debate of commonwealth affairs," +
                                    "You would say it hath been all in all his study:" +
                                    "List his discourse of war, and you shall hear" +
                                    "A fearful battle render'd you in music:" +
                                    "Turn him to any cause of policy," +
                                    "The Gordian knot of it he will unloose," +
                                    "Familiar as his garter: that, when he speaks," +
                                    "The air, a charter'd libertine, is still," +
                                    "And the mute wonder lurketh in men's ears," +
                                    "To steal his sweet and honey'd sentences;" +
                                    "So that the art and practic part of life" +
                                    "Must be the mistress to this theoric:" +
                                    "Which is a wonder how his grace should glean it," +
                                    "Since his addiction was to courses vain," +
                                    "His companies unletter'd, rude and shallow," +
                                    "His hours fill'd up with riots, banquets, sports," +
                                    "And never noted in him any study," +
                                    "Any retirement, any sequestration" +
                                    "From open haunts and popularity.",

                            "I come no more to make you laugh: things now," +
                                    "That bear a weighty and a serious brow," +
                                    "Sad, high, and working, full of state and woe," +
                                    "Such noble scenes as draw the eye to flow," +
                                    "We now present. Those that can pity, here" +
                                    "May, if they think it well, let fall a tear;" +
                                    "The subject will deserve it. Such as give" +
                                    "Their money out of hope they may believe," +
                                    "May here find truth too. Those that come to see" +
                                    "Only a show or two, and so agree" +
                                    "The play may pass, if they be still and willing," +
                                    "I'll undertake may see away their shilling" +
                                    "Richly in two short hours. Only they" +
                                    "That come to hear a merry bawdy play," +
                                    "A noise of targets, or to see a fellow" +
                                    "In a long motley coat guarded with yellow," +
                                    "Will be deceived; for, gentle hearers, know," +
                                    "To rank our chosen truth with such a show" +
                                    "As fool and fight is, beside forfeiting" +
                                    "Our own brains, and the opinion that we bring," +
                                    "To make that only true we now intend," +
                                    "Will leave us never an understanding friend." +
                                    "Therefore, for goodness' sake, and as you are known" +
                                    "The first and happiest hearers of the town," +
                                    "Be sad, as we would make ye: think ye see" +
                                    "The very persons of our noble story" +
                                    "As they were living; think you see them great," +
                                    "And follow'd with the general throng and sweat" +
                                    "Of thousand friends; then in a moment, see" +
                                    "How soon this mightiness meets misery:" +
                                    "And, if you can be merry then, I'll say" +
                                    "A man may weep upon his wedding-day.",

                            "First, heaven be the record to my speech!" +
                                    "In the devotion of a subject's love," +
                                    "Tendering the precious safety of my prince," +
                                    "And free from other misbegotten hate," +
                                    "Come I appellant to this princely presence." +
                                    "Now, Thomas Mowbray, do I turn to thee," +
                                    "And mark my greeting well; for what I speak" +
                                    "My body shall make good upon this earth," +
                                    "Or my divine soul answer it in heaven." +
                                    "Thou art a traitor and a miscreant," +
                                    "Too good to be so and too bad to live," +
                                    "Since the more fair and crystal is the sky," +
                                    "The uglier seem the clouds that in it fly." +
                                    "Once more, the more to aggravate the note," +
                                    "With a foul traitor's name stuff I thy throat;" +
                                    "And wish, so please my sovereign, ere I move," +
                                    "What my tongue speaks my right drawn sword may prove.",

                            "Now is the winter of our discontent" +
                                    "Made glorious summer by this sun of York;" +
                                    "And all the clouds that lour'd upon our house" +
                                    "In the deep bosom of the ocean buried." +
                                    "Now are our brows bound with victorious wreaths;" +
                                    "Our bruised arms hung up for monuments;" +
                                    "Our stern alarums changed to merry meetings," +
                                    "Our dreadful marches to delightful measures." +
                                    "Grim-visaged war hath smooth'd his wrinkled front;" +
                                    "And now, instead of mounting barded steeds" +
                                    "To fright the souls of fearful adversaries," +
                                    "He capers nimbly in a lady's chamber" +
                                    "To the lascivious pleasing of a lute." +
                                    "But I, that am not shaped for sportive tricks," +
                                    "Nor made to court an amorous looking-glass;" +
                                    "I, that am rudely stamp'd, and want love's majesty" +
                                    "To strut before a wanton ambling nymph;" +
                                    "I, that am curtail'd of this fair proportion," +
                                    "Cheated of feature by dissembling nature," +
                                    "Deformed, unfinish'd, sent before my time" +
                                    "Into this breathing world, scarce half made up," +
                                    "And that so lamely and unfashionable" +
                                    "That dogs bark at me as I halt by them;" +
                                    "Why, I, in this weak piping time of peace," +
                                    "Have no delight to pass away the time," +
                                    "Unless to spy my shadow in the sun" +
                                    "And descant on mine own deformity:" +
                                    "And therefore, since I cannot prove a lover," +
                                    "To entertain these fair well-spoken days," +
                                    "I am determined to prove a villain" +
                                    "And hate the idle pleasures of these days." +
                                    "Plots have I laid, inductions dangerous," +
                                    "By drunken prophecies, libels and dreams," +
                                    "To set my brother Clarence and the king" +
                                    "In deadly hate the one against the other:" +
                                    "And if King Edward be as true and just" +
                                    "As I am subtle, false and treacherous," +
                                    "This day should Clarence closely be mew'd up," +
                                    "About a prophecy, which says that 'G'" +
                                    "Of Edward's heirs the murderer shall be." +
                                    "Dive, thoughts, down to my soul: here" +
                                    "Clarence comes.",

                            "To bait fish withal: if it will feed nothing else," +
                                    "it will feed my revenge. He hath disgraced me, and" +
                                    "hindered me half a million; laughed at my losses," +
                                    "mocked at my gains, scorned my nation, thwarted my" +
                                    "bargains, cooled my friends, heated mine" +
                                    "enemies; and what's his reason? I am a Jew. Hath" +
                                    "not a Jew eyes? hath not a Jew hands, organs," +
                                    "dimensions, senses, affections, passions? fed with" +
                                    "the same food, hurt with the same weapons, subject" +
                                    "to the same diseases, healed by the same means," +
                                    "warmed and cooled by the same winter and summer, as" +
                                    "a Christian is? If you prick us, do we not bleed?" +
                                    "if you tickle us, do we not laugh? if you poison" +
                                    "us, do we not die? and if you wrong us, shall we not" +
                                    "revenge? If we are like you in the rest, we will" +
                                    "resemble you in that. If a Jew wrong a Christian," +
                                    "what is his humility? Revenge. If a Christian" +
                                    "wrong a Jew, what should his sufferance be by" +
                                    "Christian example? Why, revenge. The villany you" +
                                    "teach me, I will execute, and it shall go hard but I" +
                                    "will better the instruction.",

                            "Virtue! a fig! 'tis in ourselves that we are thus" +
                                    "or thus. Our bodies are our gardens, to the which" +
                                    "our wills are gardeners: so that if we will plant" +
                                    "nettles, or sow lettuce, set hyssop and weed up" +
                                    "thyme, supply it with one gender of herbs, or" +
                                    "distract it with many, either to have it sterile" +
                                    "with idleness, or manured with industry, why, the" +
                                    "power and corrigible authority of this lies in our" +
                                    "wills. If the balance of our lives had not one" +
                                    "scale of reason to poise another of sensuality, the" +
                                    "blood and baseness of our natures would conduct us" +
                                    "to most preposterous conclusions: but we have" +
                                    "reason to cool our raging motions, our carnal" +
                                    "stings, our unbitted lusts, whereof I take this that" +
                                    "you call love to be a sect or scion.",

                            "Blow, winds, and crack your cheeks! rage! blow!" +
                                    "You cataracts and hurricanoes, spout" +
                                    "Till you have drench'd our steeples, drown'd the cocks!" +
                                    "You sulphurous and thought-executing fires," +
                                    "Vaunt-couriers to oak-cleaving thunderbolts," +
                                    "Singe my white head! And thou, all-shaking thunder," +
                                    "Smite flat the thick rotundity o' the world!" +
                                    "Crack nature's moulds, an germens spill at once," +
                                    "That make ingrateful man!"
                    };

            /**
             * Our data, part 3.
             */
            private boolean[] mExpanded =
                    {
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false
                    };

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                SpeechView sv;
                if(convertView == null){
                    sv = new SpeechView(mContext,mTitles[position],mDialogue[position],mExpanded[position]);
                }else{
                    sv = (SpeechView) convertView;
                    sv.setTitle(mTitles[position]);
                    sv.setDialogue(mDialogue[position]);
                    sv.setExpanded(mExpanded[position]);
                }
                return sv;
            }

            public void toggle(int position){
                mExpanded[position] = !mExpanded[position];
                notifyDataSetChanged();
            }
        }

        private class SpeechView extends LinearLayout{

            private TextView mTitle;
            private TextView mDialogue;

            public SpeechView(Context context,String title,String dialogue,boolean expanded){
                super(context);
                this.setOrientation(VERTICAL);

                mTitle = new TextView(context);
                mTitle.setText(title);
                addView(mTitle, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                mDialogue = new TextView(context);
                mDialogue.setText(dialogue);
                addView(mDialogue, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                mDialogue.setVisibility(expanded ? VISIBLE : GONE);
            }

            public void setTitle(String text){
                mTitle.setText(text);
            }

            public void setDialogue(String words){
                mDialogue.setText(words);
            }

            public void setExpanded(boolean expanded){
                mDialogue.setVisibility(expanded ? VISIBLE:GONE);
            }
        }
    }

    public static class List8 extends  ListActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_8);
        }
    }
}
