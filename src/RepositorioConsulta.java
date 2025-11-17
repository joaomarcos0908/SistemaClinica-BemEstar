import java.util.ArrayList;
import java.util.List;

public class RepositorioConsulta {

List<Consulta> consultas=new ArrayList<>();
    public void adicionar(Consulta c){
  consultas.add(c);
    }
public List<Consulta> listarConsultas(){
        return consultas;
}

public  boolean remover(int id){
 for(Consulta c: consultas){
     if(c.getId()== id){
         consultas.remove(c);
         return true;
     }
 }
 return false;
}
public Boolean procurarConsultaPorId(int id){
        for(Consulta c: consultas){
            if(c.getId()==id){
                System.out.println(consultas.get(c.getId()));
                return true;
            }
        }
        return false;
}

}
