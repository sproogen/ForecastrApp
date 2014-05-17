package com.jamesg.windforecast;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jamesg.windforecast.data.Spot;

import java.util.List;

public class DrawerListFragment extends ListFragment {

    private SampleAdapter adapter;
    private String openSpot = "";
    private int selected = 0;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        selected = pos;
        Spot item = adapter.getItem(pos);
        if(pos == 0){
            if(getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).closeSpot();
            }
        }else if(item.getType() == 2){
            if(item.getLabel().equals("Update")){
                if(getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).checkForUpdates(true);
                    ((MainActivity) getActivity()).closeSpot();
                    ((MainActivity) getActivity()).toggle();
                }
            }
        }else if(item.getType() == 0){
            getListView().setItemChecked(pos, true);
            if(getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadSpot(item.getName(),1);
            }
        }
    }

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new SampleAdapter(getActivity(), MainActivity.data.getAllSpots(0));
		setListAdapter(adapter);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setDivider(null);
        getListView().setItemChecked(0, true);
    }

    public void setSpot(String name){
        //closeSpot();
        this.openSpot = name;
        for(int i=0;i<getListView().getCount();i++){
            if(adapter.getItem(i).getType() == 0){
                if(adapter.getItem(i).getName().equals(name)){
                    getListView().setItemChecked(i, true);
                    selected = i;
                    break;
                }
            }
        }
        adapter.setSpot(name);
    }

    public void refreshSpots(){
        adapter.setData(MainActivity.data.getAllSpots(0));
    }

    public void closeSpot(){
        this.openSpot = "";
        selected = 0;
        adapter.closeSpot();
        /*for(int i=0;i<getListView().getCount();i++){
            getListView().setItemChecked(i, false);
        }*/
        getListView().setItemChecked(0, true);
    }

	public class SampleAdapter extends ArrayAdapter<Spot> {

        private String openSpot = "";

		public SampleAdapter(Context context, List data) {
			super(context, 0, data);
		}

        public void setData(List data){
            //this.clear();
            //this.addAll(data);
            this.notifyDataSetChanged();
        }

        public int getCount(){
            return super.getCount()+6;
        }

        public Spot getItem(int position){
            if(position == 0){
                return new Spot("All Spots","menu");
            }else if(position == 1){
                return new Spot("Favourites","header");
            }else if(position == getCount()-4){
                return new Spot("Settings","header");
            }else if(position == getCount()-3){
                return new Spot("App Settings","menu");
            }else if(position == getCount()-2){
                return new Spot("Update","menu");
            }else if(position == getCount()-1){
                return new Spot("About","menu");
            }else{
                return super.getItem(position-2);
            }
        }

		public View getView(int position, View convertView, ViewGroup parent) {

			if (getItem(position).getType() == 1) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_header, null);
                TextView title = (TextView) convertView.findViewById(R.id.header);
                title.setText(getItem(position).getLabel());
            }else if(getItem(position).getType() == 2){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
                TextView title = (TextView) convertView.findViewById(R.id.row_title);
                title.setText(getItem(position).getLabel());
            }else{
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_spot, null);
                TextView title = (TextView) convertView.findViewById(R.id.row_title);
                final Spot s = getItem(position);
                title.setText(s.getName());
                ImageView delete = (ImageView)convertView.findViewById(R.id.deleteButton);
                delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.ic_action_cancel)
                                .setTitle("Remove from favourites")
                                .setMessage("Are you sure you want to remove this spot from favourites.")
                                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(getActivity() instanceof MainActivity) {
                                            ((MainActivity) getActivity()).removeSpot(s);
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                });
            }

			return convertView;
		}

        @Override
        public boolean isEnabled(int position) {

            if (getItem(position).getType() == 1) {
                return false;
            }else if(selected == position){
                return false;
            }else if(getItem(position).getType() == 2){
                if(getItem(position).getLabel().equals("About")){
                    return false;
                }else if(getItem(position).getLabel().equals("App Settings")){
                    return false;
                }
            }
            /*(openSpot.equals("") && position == 0){
                return false;
            }else if (getItem(position).getType() == 2){
                return true;
            }else if(getItem(position).getName().equals(openSpot)){
                return false;
            }*/
            return true;
        }

        public void setSpot(String name){
            this.openSpot = name;
        }

        public void closeSpot(){
            this.openSpot = "";
        }
	}
}
