package com.example.next.gplussignin.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.next.gplussignin.MainActivity;
import com.example.next.gplussignin.R;
import com.example.next.gplussignin.controller.UpdateController;
import com.example.next.gplussignin.fragment.HomeFragment;
import com.example.next.gplussignin.interfaces.IHomeList;
import com.example.next.gplussignin.model.HomeModel;
import com.example.next.gplussignin.utility.FileWriteNRead;
import com.example.next.gplussignin.utility.NetworkCheck;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by next on 13/2/17.
 */
public class HomeAdapter extends BaseAdapter
{
    Context mContext;
    ArrayList<HomeModel> mModelArrayList;
    LayoutInflater mInflater;
    EditText mExtPopupdes, mExtPopupTitle;
    Button mBtnPopupDone, mBtnPopupCancel;
    public HomeAdapter(Context mContext, ArrayList<HomeModel> mModelArrayList) {
        this.mContext = mContext;
        this.mModelArrayList = mModelArrayList;
        mInflater = LayoutInflater.from(mContext);

    }


    @Override
    public int getCount() {
        return mModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
        view = mInflater.inflate(R.layout.item_list_details, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        HomeModel model = (HomeModel) getItem(position);
        //title
        if (model.getTitle() != null) {
            if (model.getTitle().length() >20) {
                String subString = model.getTitle().substring(0, 20)+"...";
                viewHolder.txtTitle.setText(subString);
            } else {
                viewHolder.txtTitle.setText(model.getTitle());
            }
        }
       //description
        if (model.getData() != null) {
            if (model.getData().length() > 40) {
                String subString = model.getData().substring(0, 40) + "...";
                viewHolder.txtdesc.setText(subString);
            } else {
                viewHolder.txtdesc.setText(model.getData());
            }
        }


        //on click of item button
        viewHolder.btnDisplayPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupFunction(position);


            }

              });
        //on click of cardview delete the item
        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Do you want to delete this Task? ");
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mModelArrayList.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(mContext, "deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
        return view;
    }

    private void popupFunction(final int position) {
        final Dialog dialog = new Dialog(mContext);
       // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewPopup = inflater.inflate(R.layout.item_list_popup, null);
        initPopupView(viewPopup);

        dialog.setTitle("Edit Title");
        dialog.setContentView(viewPopup);
        dialog.setCancelable(false);
        mExtPopupdes.setText(mModelArrayList.get(position).getData());
        mExtPopupTitle.setText(mModelArrayList.get(position).getTitle());
        dialog.show();

       Toast.makeText(mContext, "position:"+position, Toast.LENGTH_SHORT).show();

        //close the dialog
       /* mBtnPopupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

        //update the description
        mBtnPopupDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final String desc =  mExtPopupdes.getText().toString();
                    final String title = mExtPopupTitle.getText().toString();
                //Toast.makeText(mContext,desc+" "+mModelArrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();


                HomeFragment.modelList(new IHomeList() {
                    @Override
                    public void getListData(ArrayList<HomeModel> homeModels) {
                        if (!desc.equals("") && !title.equals("") ) {
                            mModelArrayList.get(position).setData(desc);
                            mModelArrayList.get(position).setTitle(title);

                            // update the file
                            FileWriteNRead update = new FileWriteNRead();
                            update.updateFile(mModelArrayList, mContext);

                            //creating the json object to store the update task
                            JSONObject jsonObject = new JSONObject();
                            try {
                                String userId = mModelArrayList.get(position).getUserId();
                                if (userId == null) {
                                    userId = "ram@gmail.com";
                                }
                                jsonObject.put("userid", "jyothishkm@helixsolutions.com");
                                jsonObject.put("time", "1233212222");
                                jsonObject.put("title", title);
                                jsonObject.put("note", desc);

                                NetworkCheck networkCheck = new NetworkCheck(mContext);
                                boolean isAvilable = networkCheck.checkInternetConn();
                                if (isAvilable) {

                                    String postUrl = mContext.getResources().getString(R.string.post_url);
                                    UpdateController controller = new UpdateController(mContext);
                                    controller.updateData(jsonObject.toString(), postUrl);
                                    notifyDataSetChanged();

                                    //HomeFragment.callAdapter(homeModels, mContext);
                                } else {
                                    SharedPreferences preferences = mContext.getSharedPreferences("updateData", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("data", jsonObject.toString());
                                    editor.apply();
                                    notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                });
            }
        });
    }

    private void initPopupView(View viewPopup) {
        mExtPopupTitle = (EditText) viewPopup.findViewById(R.id.edit_popup_titile);
        mExtPopupdes = (EditText) viewPopup.findViewById(R.id.edit_popup_des);
       // mBtnPopupCancel = (Button) viewPopup.findViewById(R.id.btn_popup_des_cancel);
        mBtnPopupDone = (Button) viewPopup.findViewById(R.id.btn_popup_des_done);
    }

    private class ViewHolder
    {
        TextView txtdesc, txtTitle;
        Button btnDisplayPopup;
        CardView cardView;

        public ViewHolder(View view) {

            txtdesc = (TextView) view.findViewById(R.id.txt_desc);
            txtTitle = (TextView) view.findViewById(R.id.txt_title);
            btnDisplayPopup = (Button) view.findViewById(R.id.btn_popup_item_list);
            cardView = (CardView) view.findViewById(R.id.cardview_item);

        }
    }



}
