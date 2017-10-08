package it.gov.spid.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.gov.spid.IdentityProviders;
import it.gov.spid.R;

/**
 * Created by matteo on 05/09/16.
 */
public class IdentityProviderAdapter extends RecyclerView.Adapter<IdentityProviderAdapter.ChatViewHolder> {
    private OnClickListener listener;
    private IdentityProviders[] providers;

    public IdentityProviderAdapter(IdentityProviders[] providers) {
        this.providers = providers;
    }

    public IdentityProviderAdapter(IdentityProviders[] providers, OnClickListener listener) {
        this.providers = providers;
        this.listener = listener;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public IdentityProviderAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.authentication_provider_list_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IdentityProviderAdapter.ChatViewHolder holder, int position) {
        holder.setIdentityProvider(providers[position]);
    }

    @Override
    public int getItemCount() {
        return providers.length;
    }

    class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imageView;
        private IdentityProviders identityProvider;

        ChatViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.imageView = itemView.findViewById(R.id.authProviderImage);
        }

        private void setIdentityProvider(IdentityProviders identityProvider){
            this.imageView.setImageResource(identityProvider.getImageDrawable());
            this.identityProvider = identityProvider;
        }

        @Override
        public void onClick(View v) {
            listener.onItemClicked(getAdapterPosition(), v, identityProvider);
        }
    }

    public interface OnClickListener {
        void onItemClicked(int position, View v, IdentityProviders identityProvider);
    }
}
