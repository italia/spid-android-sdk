package it.gov.spid;

/**
 * Created by matteo on 07/10/17.
 */

public enum IdentityProviders {
    ARUBA("Aruba ID", "arubaid", R.drawable.ic_spid_idp_posteid),
    NAMIRIAL("Namirial ID", "namirialid", R.drawable.ic_spid_idp_posteid),
    INFOCERT("Infocert ID", "infocertid", R.drawable.ic_spid_idp_posteid),
    POSTE("Poste ID", "posteid", R.drawable.ic_spid_idp_posteid),
    SIELTE("Sielte ID", "sielteid", R.drawable.ic_spid_idp_posteid);

    private final String name;
    private final String identityProvider;
    private final int imageDrawable;

    IdentityProviders(String name, String identityProvider, int imageDrawable) {
        this.name = name;
        this.identityProvider = identityProvider;
        this.imageDrawable = imageDrawable;
    }

    public String getName() {
        return name;
    }

    public String getIdentityProvider() {
        return identityProvider;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }
}
