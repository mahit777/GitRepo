package myprivate.githubrepo.Adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import myprivate.githubrepo.Activities.UserDetails;
import myprivate.githubrepo.Model.Repository;
import myprivate.githubrepo.Model.User;
import myprivate.githubrepo.R;
import myprivate.githubrepo.Activities.RepoDetails;

import static myprivate.githubrepo.Model.Repository.REPO_TYPE;
import static myprivate.githubrepo.Model.User.USER_TYPE;

/**
 * Created by mahitej on 15-12-2017.
 */

public class RecycleCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    ArrayList<Object>list;
    String tag;

    public RecycleCustomAdapter(Context context, ArrayList<Object> list, String tag) {
        this.context = context;
        this.list = list;
        this.tag = tag;
    }
    public ArrayList<Object> getList()  {  return list;  }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position)instanceof User ) return USER_TYPE;
        else if(list.get(position)instanceof Repository) return REPO_TYPE;
        else return -1;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case REPO_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_repo_item, parent, false);
                return new RepoHolder(view);
                }
            case USER_TYPE:{
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contributor_thumbnail, parent, false);
                return new UserHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType())
        {

            case REPO_TYPE:
            RepoHolder repoHolder = (RepoHolder) holder;
            if(tag.equals(UserDetails.TAG))
                repoHolder.bind((Repository) list.get(position),true);
            else {
                repoHolder.bind((Repository) list.get(position));
            }
            break;

            case USER_TYPE:
                UserHolder userHolder=(UserHolder) holder;
                userHolder.bind((User) list.get(position));


        }
    }
    class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView userThumbnail;
        TextView name;
        String imageUrl;
        LinearLayout parent;
        public UserHolder(View itemView) {
            super(itemView);
            parent=(LinearLayout)itemView.findViewById(R.id.contributor_grid_element);
            name=(TextView)itemView.findViewById(R.id.contributor_name);
            userThumbnail=(ImageView)itemView.findViewById(R.id.contributor_image);
        }


        void bind(User userModel){
            imageUrl=userModel.getAvatar_url();
            Picasso.with(context).load(imageUrl).into(userThumbnail);
            parent.setOnClickListener(this);
            name.setText(userModel.getLogin());
        }

        @Override
        public void onClick(View view) {
        switch (view.getId()){
            case R.id.contributor_grid_element:{
                Intent intent=new Intent(context,UserDetails.class);
                intent.putExtra(context.getString(R.string.get_user_url),name.getText().toString().trim());
                intent.putExtra(context.getString(R.string.repo_image),imageUrl);
                context.startActivity(intent);
            }
        }
        }
    }
    class RepoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView repoName,fullName,watchersCount,starCount,forkCount;
        ImageView repoImage;
        String description,projectLink,imageUrl;
        CardView cardView;

        RepoHolder(View itemView) {
            super(itemView);

            repoName  = (TextView)itemView.findViewById(R.id.repo_name);
            fullName   = (TextView)itemView.findViewById(R.id.repo_full_name);
//            description    = (TextView)itemView.findViewById(R.id.repo_description);
            watchersCount = (TextView)itemView.findViewById(R.id.watchers_count);
            starCount    = (TextView) itemView.findViewById(R.id.star_count);
            forkCount    = (TextView) itemView.findViewById(R.id.repo_forked);
            repoImage   =(ImageView) itemView.findViewById(R.id.repo_image);
            cardView =(CardView)itemView.findViewById(R.id.row_homeRecycler);


        }
        void bind(Repository repoModel){
            imageUrl=repoModel.getOwner().getAvatar_url();
            Picasso.with(context).load(imageUrl).into(repoImage);
            repoName.setText(repoModel.getName());
            fullName.setText(repoModel.getFull_name());
            watchersCount.setText(repoModel.getWatchers().toString());
            projectLink=repoModel.getHtml_url();
            forkCount.setText(repoModel.getForks().toString());
            starCount.setText(repoModel.getStargazers_count().toString());
            description= repoModel.getDescription();
            cardView.setOnClickListener(this);
        }
        void bind(Repository repoModel,Boolean check){
            imageUrl=repoModel.getOwner().getAvatar_url();
            repoImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.octocat));
            repoName.setText(repoModel.getName());
            fullName.setText(repoModel.getFull_name());
            watchersCount.setText(repoModel.getWatchers().toString());
            projectLink=repoModel.getHtml_url();
            forkCount.setText(repoModel.getForks().toString());
            starCount.setText(repoModel.getStargazers_count().toString());
            description= repoModel.getDescription();
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.row_homeRecycler:{
                    Intent intent=new Intent(context, RepoDetails.class);
                    intent.putExtra(context.getString(R.string.repo_description),description);
                  intent.putExtra(context.getString(R.string.repo_image),imageUrl);
                    intent.putExtra(context.getString(R.string.repo_name),repoName.getText().toString().trim());
                    intent.putExtra(context.getString(R.string.html_url),projectLink);
                    intent.putExtra(context.getString(R.string.repo_contributors),fullName.getText().toString().trim());

                    context.startActivity(intent);


                }
            }

        }
    }

    @Override
    public int getItemCount() {
        if(list!=null)
        return list.size();
        else return 0;
    }
}
