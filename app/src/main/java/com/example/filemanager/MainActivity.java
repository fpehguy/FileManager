package com.example.filemanager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    ImageButton b1;
        Button refreshButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout1);
        b1=findViewById(R.id.b1);
        findViewById(R.id.goBack).setEnabled(false);
          }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

        }
        return true;
    }

    class TextAdapter extends BaseAdapter{
        private boolean[] selection;


        private List<String> data =new ArrayList<>();

        public void setData(List<String> data){
            if(data!=null){
                this.data.clear();
                if(data.size() > 0){
                    this.data.addAll(data);
                }
                notifyDataSetChanged();
            }
        }
        void setSelection(boolean[] selection){
            if(selection!=null){
                this.selection=new boolean[selection.length];
                for(int i=0;i<selection.length;i++){
                    this.selection[i]=selection[i];
                }
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public String getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
               convertView.setTag(new ViewHolder((TextView)convertView.findViewById(R.id.textItem)));

            }
            ViewHolder holder= (ViewHolder) convertView.getTag();
            final String item = getItem(position);
            holder.info.setText(item.substring(item.lastIndexOf('/')+1));
            if(selection !=null){
                if(selection[position]){
                    holder.info.setBackgroundColor(Color.argb(100,9,9,9));
                }
                else{
                    holder.info.setBackgroundColor(Color.WHITE);
                }

            }
            return convertView;
        }
        class ViewHolder{
            TextView info;
            ViewHolder(TextView info){
                this.info=info;

            }


        }
    }

    private static final int REQUEST_PERMISSIONS = 1234;
    private String currentPath;
    private static final String[]PERMISSIONS={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSIONS_COUNT=2;

    @SuppressLint("NewApi")
    private boolean arePermissionsDenied(){
        int p = 0;
        while(p < PERMISSIONS_COUNT){
            if(checkSelfPermission(PERMISSIONS[p]) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
            p++;
        }

        return false;
    }

    private boolean isFileManagerInitialized = false;
    private boolean[] selection;
    private File files[];
    List<String> filesList;
    private int filesFoundCount;

    public String changerPath(){

     return "";
    }


private File dir;
    boolean isLongClick;
    private int selectIndexItem;
    private String copyPath;
    @Override
    protected void onResume() {



       // Toast.makeText(this,"ok"+changerPath(),Toast.LENGTH_SHORT).show();
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()){
                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
                return;
        }
        if(!isFileManagerInitialized) {
            //DIRECTORY_DOWNLOADS
            //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
           // final String rootPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            final String rootPath = String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath());
            currentPath=rootPath;
            dir=new File(rootPath);
             files = dir.listFiles();
            final TextView pathOutput =findViewById(R.id.pathOutput);
            pathOutput.setText(rootPath.substring(rootPath.lastIndexOf('/')+1));
            filesFoundCount = files.length;

            final ListView listView=findViewById(R.id.listView);
            final TextAdapter textAdapter1=new TextAdapter();
            listView.setAdapter(textAdapter1);

            filesList = new ArrayList<>();
            for(int i = 0; i < filesFoundCount; i++){
                filesList.add(String.valueOf(files[i].getAbsolutePath()));
            }
            textAdapter1.setData(filesList);
            selection = new boolean[files.length];

            refreshButton =findViewById(R.id.refresh);
            refreshButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    files = dir.listFiles();
                    filesFoundCount=files.length;
                    filesList.clear();
                    for(int i = 0;i < filesFoundCount; i++){
                        filesList.add(String.valueOf(files[i].getAbsolutePath()));
                    }
                    textAdapter1.setData(filesList);


                }
            });

            final ImageButton goBack =findViewById(R.id.goBack);
            goBack.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v) {
                    if( currentPath.substring(currentPath.lastIndexOf('/')+1).equals("storage")){
                        goBack.setEnabled(false);
                        return;
                    }

                    currentPath = currentPath.substring(0,currentPath.lastIndexOf('/'));
                    dir = new File(currentPath);
                    pathOutput.setText(currentPath.substring(currentPath.lastIndexOf('/')+1));
                    refreshButton.callOnClick();

                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!isLongClick){
                                if(position> files.length){
                                    return;
                                }
                                if(files[position].isDirectory()){
                                    currentPath = files[position].getAbsolutePath();
                                    dir=new File(currentPath);
                                    pathOutput.setText(currentPath.substring(currentPath.lastIndexOf('/')+1));
                                    refreshButton.callOnClick();
                                }
                                else{
                                    Toast.makeText(MainActivity.this,"Ne peut pas ouvrir ! ",Toast.LENGTH_SHORT).show();
                                }
                            }                                                 }
                    },50);
                    goBack.setEnabled(true);

                }
            });

            final LinearLayout linear=findViewById(R.id.linear);
            linear.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    //findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                }
            });



            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    isLongClick=true;
                    selection[position]=!selection[position];
                    textAdapter1.setSelection(selection);
                    boolean isAtleastOneSelected = false;
                    int selectionCount = 0;
                    for (boolean aSelection : selection) {
                        if (aSelection) {
//                            isAtleastOneSelected = true;
//                            break;
                            selectionCount++;
                        }
                    }

                    if(selectionCount>0){
                        if(selectionCount==1){
                            selectIndexItem=position;
                            findViewById(R.id.rename).setVisibility(View.VISIBLE);
                            findViewById(R.id.b1).setVisibility(View.VISIBLE);
                            findViewById(R.id.copy).setVisibility(View.VISIBLE);
                        }
                        else{
                            findViewById(R.id.rename).setVisibility(View.GONE);
                            findViewById(R.id.b1).setVisibility(View.GONE);
                            findViewById(R.id.copy).setVisibility(View.GONE);
                            findViewById(R.id.paste).setVisibility(View.GONE);

                        }
                        findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
                    }else{
                        findViewById(R.id.bottomBar).setVisibility(View.GONE);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isLongClick=false;
                        }
                    },1000);
                    return false;
                }
            });


//            final Button w=findViewById(R.id.w);
//            w.setOnClickListener(new View.OnClickListener() {
//                                     @Override
//                                     public void onClick(View v) {
////                                         Intent i=getPackageManager().getLaunchIntentForPackage("com.whatsapp");
////                                         startActivity(i);
//                                         Intent i=new Intent();
//                                         i.setAction(Intent.ACTION_SEND);
//                                         i.putExtra(Intent.EXTRA_TEXT,"JGJHGJH");
//                                         i.setType("text/plain");
//                                         startActivity(Intent.createChooser(i,"Send"));
//
//                                     }
//                                 });


                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(MainActivity.this);
                            deleteDialog.setTitle("Effacer");
                            deleteDialog.setMessage("Voulez - vous Supprime ?");
                            deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    for (int i = 0; i < files.length; i++) {
                                        if (selection[i]) {
                                            deleteFileOrFolder(files[i]);
                                            selection[i] = false;
                                        }
                                    }
                                    files = dir.listFiles();
                                    filesFoundCount = files.length;
                                    filesList.clear();
                                    for (int i = 0; i < filesFoundCount; i++) {
                                        filesList.add(String.valueOf(files[i].getAbsolutePath()));
                                    }
                                    textAdapter1.setData(filesList);
                                    findViewById(R.id.bottomBar).setVisibility(View.GONE);
                                }
                            });
                            deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            deleteDialog.show();
                        }
                    });
        final ImageButton createNewFolder =findViewById(R.id.newFolder);
            createNewFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder newFolderDialog = new AlertDialog.Builder(MainActivity.this);
                    newFolderDialog.setTitle("New Folder");
                    final EditText input=new EditText(MainActivity.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    newFolderDialog.setView(input);
                    newFolderDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final File newFolder = new File(currentPath+"/"+input.getText());
                            if(!newFolder.exists()){
                                newFolder.mkdir();
                                refreshButton.callOnClick();

                            }
                        }
                    });
                    newFolderDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           dialog.cancel();
                        }
                    });
                    newFolderDialog.show();
                }
            });

            final ImageButton rename=findViewById(R.id.rename);
            rename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder renamedialog = new AlertDialog.Builder(MainActivity.this);
                    renamedialog.setTitle("Renommer ");
                    final EditText input=new EditText(MainActivity.this);
                    final String renamePath=files[selectIndexItem].getAbsolutePath();
                    input.setText(renamePath.substring(renamePath.lastIndexOf('/')));
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    renamedialog.setView(input);
                    renamedialog.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String s =new File(renamePath).getParent()+ "/" +input.getText();
                            File newFile=new File(s);
                            new File(renamePath).renameTo(newFile);
                            refreshButton.callOnClick();
//                            selection=new boolean[files.length];
//                            textAdapter1.setSelection(selection);
                                                   }
                    });
                    renamedialog.show();
                }
            });

            final ImageButton copy=findViewById(R.id.copy);
            copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     copyPath=files[selectIndexItem].getAbsolutePath();
//                    selection=new boolean[files.length];
//                    textAdapter1.setSelection(selection);

                    findViewById(R.id.paste).setVisibility(View.VISIBLE);
                    findViewById(R.id.b1).setVisibility(View.GONE);
                    findViewById(R.id.rename).setVisibility(View.GONE);
                    findViewById(R.id.copy).setVisibility(View.GONE);

                }
            });

            final ImageButton pasteButton=findViewById(R.id.paste);
            pasteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pasteButton.setVisibility(View.GONE);
                    String dstPath = currentPath + "/" + copyPath.substring(copyPath.lastIndexOf('/'));
                    copyFile(new File(copyPath), new File(dstPath));
                    refreshButton.callOnClick();
                    findViewById(R.id.bottomBar).setVisibility(View.GONE);
                }
            });


            isFileManagerInitialized = true;
        }else{
            refreshButton.callOnClick();
        }
    }
    private void copyFile(File src,File dst){
        try{
        InputStream in=new FileInputStream(src);
        OutputStream out=new FileOutputStream(dst);
        byte [] buf =new byte[1024];
        int len;
        while((len=in.read(buf))>0){
            out.write(buf,0,len);
            out.close();
            in.close();
        }
        }catch (FileNotFoundException e){
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void deleteFileOrFolder(File fileOrFolder){

        if(fileOrFolder.isDirectory()){
            if(fileOrFolder.list().length==0){
                fileOrFolder.delete();
            }
            else{
                String files[] = fileOrFolder.list();
                for(String temp:files){
                    File fileToDelete = new File(fileOrFolder,temp);
                    deleteFileOrFolder(fileToDelete);
                }
                if(fileOrFolder.list().length==0){
                    fileOrFolder.delete();
                }
            }
        }else{
            fileOrFolder.delete();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_PERMISSIONS && grantResults.length > 0){
            if(arePermissionsDenied()){
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }
            else{
                onResume();
            }
        }
    }
}
