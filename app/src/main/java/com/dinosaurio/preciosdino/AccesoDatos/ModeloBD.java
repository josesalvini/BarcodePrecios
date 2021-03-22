package com.dinosaurio.preciosdino.AccesoDatos;

import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.Parametro;
import com.dinosaurio.preciosdino.Entidades.Sucursal;
import com.dinosaurio.preciosdino.Entidades.TipoEtiqueta;
import com.dinosaurio.preciosdino.Entidades.Usuario;

import io.realm.annotations.RealmModule;

// Create the module
@RealmModule(classes = { Usuario.class, CargaCodigos.class , Parametro.class, TipoEtiqueta.class, Sucursal.class})

public class ModeloBD {

    public ModeloBD() {
    }
}