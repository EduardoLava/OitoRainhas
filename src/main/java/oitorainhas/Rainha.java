package oitorainhas;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Rainha {

	private String nome;
	private Integer linha;
	private Integer coluna;
	
	public Rainha(String nome, Integer linha, Integer coluna) {
		super();
		this.nome = nome;
		this.linha = linha;
		this.coluna = coluna;
	}

	
}
