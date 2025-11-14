public abstract class Pessoa {
    private String nome;
    private String cpf;
    private int idade;
    private String email;
    private String numTelefone;
    private String endereco;

    public Pessoa(String nome, String cpf, int idade, String email, String numTelefone, String endereco) {
        setNome(nome);
        setCpf(cpf);
        setIdade(idade);
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

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        if(idade < 0){
            throw new IllegalArgumentException("Insira uma idade válida.");
        }
        this.idade = idade;
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


