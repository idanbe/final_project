package com.example.administrator.game_4_in_a_row;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class Game_history extends AppCompatActivity {

    private Intent intent;
    private DAL dal;
    private TableLayout HistoryTable ;
    private TableRow tableRow;
    private ArrayList<Row> rowArrayList ;
    private Button resetTable ;
    private final String SETTING_KEY_SOUND = "SETTING_KEY_SOUND";
    private final String SHARED_PREFERENCES_NAME = "ShardPreferences_setting";
    private final String ON = "on";


    private static final int MAX_RESULT = 10;


    //object like struct
    static class RowHolder {
        TextView name;
        TextView win;
        TextView loss;
        TextView standoff;
        TextView percent_win;
    }

    public Game_history(DAL dal){
        this.dal = dal;
    }

    public Game_history(){

    }
    // add row
    public void addToHistoryTable(String name , int win , int loss , int draws , double percent ){

        RowHolder rowHolder = new RowHolder();
        tableRow = new TableRow(this);

        rowHolder.name = new TextView(this);
        rowHolder.win = new TextView(this);
        rowHolder.loss = new TextView(this);
        rowHolder.standoff = new TextView(this);
        rowHolder.percent_win = new TextView(this);

        // name col
        rowHolder.name.setText(name);
        rowHolder.name.setGravity(Gravity.LEFT);

        // win col
        rowHolder.win.setText(Integer.toString(win));
        rowHolder.win.setGravity(Gravity.CENTER);

        // loss col
        rowHolder.loss.setText(Integer.toString(loss));
        rowHolder.loss.setGravity(Gravity.CENTER);

        // standoff col
        rowHolder.standoff.setText(Integer.toString(draws));
        rowHolder.standoff.setGravity(Gravity.CENTER);

        // percent_win col
        rowHolder.percent_win.setText(percent + "%");
        rowHolder.percent_win.setGravity(Gravity.RIGHT);

        // add to row
        tableRow.addView(rowHolder.name);
        tableRow.addView(rowHolder.win);
        tableRow.addView(rowHolder.loss);
        tableRow.addView(rowHolder.standoff);
        tableRow.addView(rowHolder.percent_win);
        // Color yellow
        tableRow.setBackgroundColor(Color.YELLOW);

        // add row to history Table
        HistoryTable.addView(tableRow);
    }

    // update
    public void upDateHistoryTable(String name , boolean ifwin , boolean ifStendOff){
        dal.upDateWinOrLoss(name, ifwin, ifStendOff);
        rowArrayList = dal.getDb();
    }

    public void removeRowFromHistory(String name){
        dal.removeRow(name);
        rowArrayList = dal.getDb();
    }

    // TODO : remove this , only for test
    public void printArray(){
        for(int i = 0 ; i < rowArrayList.size() ; i++){
            Row row = rowArrayList.get(i);
            System.out.println(row.getName() + " , " + row.getWin() + " , " + row.getLoss() + " , " + row.getDraws() + " , " + row.getPercent_Win() + "%" );
        }
    }

    public int sortArrayList(){
        int indexOfMakValue = 0 ;
        double maxPercent = 0 ;
        int maxNumberOfWin = 0;
        for(int i = 0 ; i < rowArrayList.size() ; i++){
            if(rowArrayList.get(i).getPercent_Win() > maxPercent){
                indexOfMakValue = i ;
                maxPercent = rowArrayList.get(i).getPercent_Win() ;
                maxNumberOfWin = rowArrayList.get(i).getWin();
            }
            else if(rowArrayList.get(i).getPercent_Win() == maxPercent && rowArrayList.get(i).getWin() > maxNumberOfWin){
                indexOfMakValue = i;
            }
        }
        return indexOfMakValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_history);

        dal = new DAL(this);

        resetTable = (Button)findViewById(R.id.resetHistory);
        HistoryTable = (TableLayout)findViewById(R.id.history_table);
        HistoryTable.setStretchAllColumns(true);

        SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(sharedpreferences.getString(SETTING_KEY_SOUND, null) == null || sharedpreferences.getString(SETTING_KEY_SOUND, null).equals(ON) ){
            MainActivity.getMusic().start();

            System.out.println("sound true !");
        }

        // DB to array list
        rowArrayList = dal.getDb();
/*****************************************/
        // print DB for test
        printArray();
/*****************************************/
        // print to history table
        int j = 0 ;
        while ( !rowArrayList.isEmpty() && j < MAX_RESULT){
            int i = sortArrayList();
            Row row = rowArrayList.get(i) ;
            addToHistoryTable(row.getName(), row.getWin(), row.getLoss(), row.getDraws(), row.getPercent_Win());
            rowArrayList.remove(i);
            j++ ;
        }
/*****************************************/
         //print to test
        System.out.println("after !!");
        printArray();
/********************************/

        resetTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dal.removeAll();
                AlertDialog alertDialog = new AlertDialog.Builder(Game_history.this).create();
                alertDialog.setTitle("Remove All History :");
                alertDialog.setMessage("All results will be deleted\n" + "Are you sure ?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                View view1 = HistoryTable.getChildAt(0);
                                View view2 = HistoryTable.getChildAt(1);
                                HistoryTable.removeAllViews();
                                HistoryTable.addView(view1);
                                HistoryTable.addView(view2);
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            intent = new Intent(Game_history.this, Settings.class);
            startActivity(intent);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
