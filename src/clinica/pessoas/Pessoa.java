package clinica.pessoas;

import java.time.LocalDate;
import java.time.Period;

public abstract class Pessoa {
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String email;
    private String numTelefone;
    private String endereco;

    public Pessoa(String nome, String cpf, LocalDate dataNascimento, String email, String numTelefone, String endereco){
        setNome(nome);
        setCpf(cpf);
        setDataNascimento(dataNascimento);
        setEmail(email);
        setNumTelefone(numTelefone);
        setEndereco(endereco);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Insira um nome.");
        }
        for (char c : nome.toCharArray()) {
            if (Character.isDigit(c)) {
                throw new IllegalArgumentException("Nome não pode possuir número.");
            }
        }
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("Insira um CPF.");
        }
            this.cpf = cpf;
        }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if(dataNascimento == null){
            throw new IllegalArgumentException("Insira uma data de nascimento.");
        }
        if (dataNascimento.isAfter(LocalDate.now())){
            throw new IllegalArgumentException("Insira uma data válida.");
        }
        this.dataNascimento = dataNascimento;
    }

    public int getIdade(){
        return Period.between(this.dataNascimento, LocalDate.now()).getYears();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(email == null || email.trim().isEmpty()){
            throw new IllegalArgumentException("Insira um email.");
        }
        this.email = email;
    }

    public String getNumTelefone() {
        return numTelefone;
    }

    public void setNumTelefone(String numTelefone) {
        if (numTelefone == null || numTelefone.trim().isEmpty()){
            throw new IllegalArgumentException("Insira um número de telefone.");
        }
        this.numTelefone = numTelefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()){
            throw new IllegalArgumentException("Insira um endereço.");
        }
        this.endereco = endereco;
    }

}


