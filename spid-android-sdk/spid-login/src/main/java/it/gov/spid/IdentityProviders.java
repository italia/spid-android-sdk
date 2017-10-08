package it.gov.spid;

/**
 * Created by matteo on 07/10/17.
 */

public enum IdentityProviders {
    //TODO authority sites
    NAMIRIAL("Namirial ID", "namirialid", "www.authority.it", R.drawable.spid_idp_namirialid),
    INFOCERT("Infocert ID", "infocertid", "www.authority.it", R.drawable.spid_idp_infocertid),
    POSTE("Poste ID", "posteid", "www.authority.it", R.drawable.spid_idp_posteid),
    SIELTE("Sielte ID", "sielteid", "www.authority.it", R.drawable.spid_idp_sielteid),
    TIM("TIM ID", "timid", "www.authority.it", R.drawable.spid_idp_timid),
    ARUBA("Aruba ID", "arubaid", "www.authority.it", R.drawable.spid_idp_aruba);

    private final String name;
    private final String identityProvider;
    private final String authority;
    private final int imageDrawable;

    IdentityProviders(String name, String identityProvider, String authority, int imageDrawable) {
        this.name = name;
        this.identityProvider = identityProvider;
        this.authority = authority;
        this.imageDrawable = imageDrawable;
    }

    public String getName() {
        return name;
    }

    public String getAuthority() {
        return authority;
    }

    public String getIdentityProvider() {
        return identityProvider;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }
}
