package com.tacticalcustomers.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
 SQLiteDatabase db;
 LinearLayout root, list;
 EditText search;
 int green=Color.rgb(58,112,82);
 @Override public void onCreate(Bundle b){super.onCreate(b); db=openOrCreateDatabase("customers.db",MODE_PRIVATE,null);db.execSQL("CREATE TABLE IF NOT EXISTS customers(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,phone TEXT,email TEXT,address TEXT,category TEXT,notes TEXT,followup TEXT,created TEXT DEFAULT CURRENT_TIMESTAMP)");home();}
 TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(Color.rgb(30,45,38));t.setPadding(12,12,12,12);return t;}
 Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);return b;}
 EditText field(String hint,String value){EditText e=new EditText(this);e.setHint(hint);e.setSingleLine(true);e.setText(value==null?"":value);return e;}
 void base(String title){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(1);root.setPadding(22,22,22,22);root.setBackgroundColor(Color.rgb(248,250,247));sc.addView(root);setContentView(sc);TextView h=text(title,26);h.setTextColor(green);root.addView(h);}
 void home(){base("Tactical Customers");Button add=button("+ Register customer");root.addView(add);add.setOnClickListener(v->edit(-1));search=field("Search by name or phone","");root.addView(search);list=new LinearLayout(this);list.setOrientation(1);root.addView(list);search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int a,int c,int f){}public void onTextChanged(CharSequence s,int a,int b,int c){refresh(s.toString());}public void afterTextChanged(android.text.Editable e){}});refresh("");}
 void refresh(String q){list.removeAllViews();Cursor c=db.rawQuery("SELECT id,name,phone,category FROM customers WHERE name LIKE ? OR phone LIKE ? ORDER BY name",new String[]{"%"+q+"%","%"+q+"%"});root.findViewById(android.R.id.content);list.addView(text(c.getCount()+" customer(s)",16));while(c.moveToNext()){final int id=c.getInt(0);Button b=button(c.getString(1)+"  •  "+(c.getString(2)==null?"":c.getString(2))+"  "+(c.getString(3)==null?"":c.getString(3)));list.addView(b);b.setOnClickListener(v->edit(id));}c.close();}
 void edit(int id){String[] cols={"name","phone","email","address","category","notes","followup"};String[] vals=new String[cols.length];if(id>=0){Cursor c=db.rawQuery("SELECT name,phone,email,address,category,notes,followup FROM customers WHERE id=?",new String[]{""+id});if(c.moveToFirst())for(int i=0;i<cols.length;i++)vals[i]=c.getString(i);c.close();}
 base(id<0?"Register customer":"Customer profile");EditText[] es=new EditText[cols.length];String[] labels={"Full name *","Phone / WhatsApp","Email","Address / location","Category (Regular / VIP)","Customer notes","Next follow-up (YYYY-MM-DD)"};for(int i=0;i<cols.length;i++){es[i]=field(labels[i],vals[i]);root.addView(es[i]);}
 Button save=button("Save customer");root.addView(save);save.setOnClickListener(v->{String name=es[0].getText().toString().trim();if(name.isEmpty()){es[0].setError("Name is required");return;}ContentValues cv=new ContentValues();for(int i=0;i<cols.length;i++)cv.put(cols[i],es[i].getText().toString().trim());if(id<0)db.insert("customers",null,cv);else db.update("customers",cv,"id=?",new String[]{""+id});Toast.makeText(this,"Customer saved",Toast.LENGTH_SHORT).show();home();});
 if(id>=0){Button delete=button("Delete customer");root.addView(delete);delete.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Delete customer?").setMessage("This cannot be undone.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{db.delete("customers","id=?",new String[]{""+id});home();}).show());}
 Button back=button("Back to customers");root.addView(back);back.setOnClickListener(v->home());
 }
 @Override public void onBackPressed(){home();}
}
