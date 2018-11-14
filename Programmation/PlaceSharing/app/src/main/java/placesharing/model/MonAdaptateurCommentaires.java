package placesharing.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import placesharing.R;

public class MonAdaptateurCommentaires extends RecyclerView.Adapter<MonAdaptateurCommentaires.ViewHolder> {

    private List<Commentaire> mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView commentaire;
        public RatingBar note;
        public TextView auteur;

        public ViewHolder(View itemView) {
            super(itemView);
            commentaire = (TextView) itemView.findViewById(R.id.textviewCommentaire);
            note = (RatingBar) itemView.findViewById(R.id.textviewNote);
            auteur = (TextView) itemView.findViewById(R.id.textviewAuteur);
        }
    }

    public MonAdaptateurCommentaires(List<Commentaire> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MonAdaptateurCommentaires.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentaire_layout,parent,false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.commentaire.setText(mDataset.get(position).getAvis());
        holder.auteur.setText(mDataset.get(position).getAuteur().getEmail());
        holder.note.setRating(((float) mDataset.get(position).getNote()));

    }

    @Override
    public int getItemCount() {
        if(mDataset == null){
            return 0;
        }
        return mDataset.size();
    }
}
