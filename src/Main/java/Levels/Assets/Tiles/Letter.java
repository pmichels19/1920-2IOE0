package Levels.Assets.Tiles;

import Graphics.OpenGL.Texture;

public enum Letter {
    A ( "letter_A.png" ),
    B ( "letter_B.png" ),
    C ( "letter_C.png" ),
    D ( "letter_D.png" ),
    E ( "letter_E.png" ),
    F ( "letter_F.png" ),
    G ( "letter_G.png" ),
    H ( "letter_H.png" ),
    I ( "letter_I.png" ),
    J ( "letter_J.png" ),
    K ( "letter_K.png" ),
    L ( "letter_L.png" ),
    M ( "letter_M.png" ),
    N ( "letter_N.png" ),
    O ( "letter_O.png" ),
    P ( "letter_P.png" ),
    Q ( "letter_Q.png" ),
    R ( "letter_R.png" ),
    S ( "letter_S.png" ),
    T ( "letter_T.png" ),
    U ( "letter_U.png" ),
    V ( "letter_V.png" ),
    W ( "letter_W.png" ),
    X ( "letter_X.png" ),
    Y ( "letter_Y.png" ),
    Z ( "letter_Z.png" ),
    A_WHITE ( "letter_A_white.png" ),
    B_WHITE ( "letter_B_white.png" ),
    C_WHITE ( "letter_C_white.png" ),
    D_WHITE ( "letter_D_white.png" ),
    E_WHITE ( "letter_E_white.png" ),
    F_WHITE ( "letter_F_white.png" ),
    G_WHITE ( "letter_G_white.png" ),
    H_WHITE ( "letter_H_white.png" ),
    I_WHITE ( "letter_I_white.png" ),
    J_WHITE ( "letter_J_white.png" ),
    K_WHITE ( "letter_K_white.png" ),
    L_WHITE ( "letter_L_white.png" ),
    M_WHITE ( "letter_M_white.png" ),
    N_WHITE ( "letter_N_white.png" ),
    O_WHITE ( "letter_O_white.png" ),
    P_WHITE ( "letter_P_white.png" ),
    Q_WHITE ( "letter_Q_white.png" ),
    R_WHITE ( "letter_R_white.png" ),
    S_WHITE ( "letter_S_white.png" ),
    T_WHITE ( "letter_T_white.png" ),
    U_WHITE ( "letter_U_white.png" ),
    V_WHITE ( "letter_V_white.png" ),
    W_WHITE ( "letter_W_white.png" ),
    X_WHITE ( "letter_X_white.png" ),
    Y_WHITE ( "letter_Y_white.png" ),
    Z_WHITE ( "letter_Z_white.png" );

    private final Texture texture;

    Letter(String filename) {
        texture = new Texture( "src/Main/Java/Textures/Alphabet/" + filename );
    }

    public Texture getTexture() {
        return texture;
    }
}
