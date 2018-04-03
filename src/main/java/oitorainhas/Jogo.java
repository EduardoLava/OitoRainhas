package oitorainhas;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Jogo {

	public static int MAX_CASAS = 8;
	public static String CASA_BRANCA = " = ";
	public static String CASA_PRETA = " # ";
	
	private List<Rainha> rainhas;

	private final Random random;
	private long totalConflitos;
	private int tentativas;
	
	public Jogo() {
		this.random = new Random();
		iniciaJogo();
	}
	
	/**
	 * main
	 */
	public void iniciaJogo(){

		System.out.println("Iniciando...");
		
		inicializaRainhas();
		desenhaTabuleiro();
		System.out.println("\n\n");
		
		tentativas = 1;

		do {
			
			totalConflitos = 0;

			rainhas.forEach(rainha -> {
				
				long conflitos = 0;
					
				conflitos += somaConflitos(rainha);

				if(conflitos != 0){
					
					tentativas ++;
					insereEmOutraCasa(rainha);
					desenhaTabuleiro();
					System.out.println("Tentativas "+tentativas+"\n\n");
					
				}
				
				totalConflitos += conflitos;
					
			});
			
		} while (totalConflitos != 0);

		
		System.out.println("Solucao encontrada em "+tentativas+" tentativas");
		
		
		
	}
	
	/**
	 * 
	 * inicializa as rainhas aleatoriamente uma em cada linha
	 * 
	 */
	private void inicializaRainhas(){
		
		rainhas = new ArrayList<Rainha>(MAX_CASAS);
		
		for(int linha = 0; linha < MAX_CASAS; linha ++){
			
			rainhas.add(
				new Rainha(
					" "+(linha + 1)+" ",
					linha, 
					random.nextInt(MAX_CASAS)
				)
			);
			
		}
		
	}
	
	/**
	 * desenha o "tabuleiro" com base na matriz e posicionamento das rainhas
	 */
	private void desenhaTabuleiro(){
		
		for(int linha = 0; linha < MAX_CASAS; linha ++){
			for(int col = 0; col < MAX_CASAS; col ++){
				
				final Integer l = linha;
				final Integer c = col;
				
				Optional<Rainha> rainha = rainhas
						.stream()
						.filter( r -> 
							r.getLinha().equals(l) 
							&& r.getColuna().equals(c)
						).findFirst();
				
				if(rainha.isPresent()){
					System.out.print(rainha.get().getNome());
				} else if((linha + col)  % 2 == 0){
					System.out.print(CASA_PRETA);
				} else 
					System.out.print(CASA_BRANCA);
				
				if(col + 1 == MAX_CASAS){
					System.out.println("");
				}
			}
			
		}
		
	}
	
	/**
	 * Método responsável por somar todos os conflitos 
	 * considerando a posicao da rainha
	 * 
	 * @param rainha
	 * @return
	 */
	private long somaConflitos(Rainha rainha){
		
		long conflitos = 0;
		
		conflitos += countConflitoLinha(rainha);
		conflitos += countConflitoColuna(rainha);
		conflitos += countConflitoDiagonalPrincipal(rainha);
		conflitos += countConflitoDiagonalSecundaria(rainha);
		
		return conflitos;
		
	}

	/**
	 * 
	 * Realiza a contagem de conflitos que ocorrem na mesma linha
	 * 
	 * @param rainha
	 * @return
	 */
	private long countConflitoLinha(Rainha rainha){
		
		return this.rainhas
				.stream()
				.filter(r ->
					!r.equals(rainha)
					&& r.getLinha().equals(rainha.getLinha())
			).count();
		
	}

	/**
	 * 
	 * conta conflitos existentes na mesma coluna
	 * 
	 * @param rainha
	 * @return
	 */
	private long countConflitoColuna(Rainha rainha){
		
		return this.rainhas
				.stream()
				.filter(r -> 
					!r.equals(rainha)
					&& r.getColuna().equals(rainha.getColuna()) 
			).count();
		
	}

	/**
	 * 
	 * Modifica a posicao da rainha para a posição que tiver menos conflitos
	 * 
	 * 
	 * @param rainha
	 */
	private void insereEmOutraCasa(Rainha rainha){
		
		int linha = rainha.getLinha();
		int coluna = -1;
		
		do {
			coluna = casaComMenosConlitos(rainha);
		} while(existeRainha(linha, coluna) );
		
		rainha.setColuna(coluna);
//		rainha.setLinha(linha);
		
	}
	
	/**
	 * 
	 * faz um comparativo com todas as casas de uma determinada linha 
	 * para saber qual possui menos conflitos
	 * 
	 * @param rainha
	 * @return
	 */
	private int casaComMenosConlitos(Rainha rainha){

		/**
		 * para evitar loop infinito
		 */
		if(tentativas > 500){
			return random.nextInt(MAX_CASAS);
		}
		
		long menorTaxaConflito = 100;
		long conflitosCasa;
		int casaMenorIndiceConflito = 0;

		for(int i = 0; i < MAX_CASAS; i ++){
			
			conflitosCasa = 0;
			
			Rainha r = new Rainha(rainha.getNome(), rainha.getLinha(), i);
			
			conflitosCasa = somaConflitos(r);
			
			if(
				!rainha.getColuna().equals(i)
				&& menorTaxaConflito > conflitosCasa
			){
				casaMenorIndiceConflito = i;
				menorTaxaConflito = conflitosCasa;
			}
			
		}
		
		return casaMenorIndiceConflito;
	}
	
	/**
	 * 
	 * Verifica se já existe alguma rainha na posição tentada 
	 * 
	 * @param linha
	 * @param coluna
	 * @return
	 */
	private boolean existeRainha(int linha, int coluna){
		return this.rainhas.stream().filter(
			r -> 
			r.getColuna().equals(coluna)
			&& r.getLinha().equals(linha)
		).findFirst()
		.isPresent();
		
	}


	/**
	 * valida se existe conflito na diagonal principal 
	 * referente a rainha
	 * 
	 * @param rainha
	 * @return
	 */
	private long countConflitoDiagonalPrincipal(Rainha rainha){
		
		long conflitos = 0;
		
//		para tras
		int linha = rainha.getLinha() - 1;
		int coluna = rainha.getColuna() - 1;
		
		while (linha >= 0 && coluna >= 0) {
			
			if(existeRainha(linha, coluna)){
				conflitos ++ ;
			}
			
			linha --;
			coluna --;
			
		}
		
		linha = rainha.getLinha() + 1;
		coluna = rainha.getColuna() + 1; 
		
//		para frente
		while (linha <= MAX_CASAS  && coluna <= MAX_CASAS) {
			
			if(existeRainha(linha, coluna)){
				conflitos ++ ;
			}
			
			linha ++;
			coluna ++;
			
		}
		
		
		return conflitos;
	}
	
	
	/**
	 * valida se existe conflito na diagonal 
	 * secundaria com referencia na rainha
	 * 
	 * @param rainha
	 * @return
	 */
	private long countConflitoDiagonalSecundaria(Rainha rainha){
		
		long conflitos = 0;
		
		int linha = rainha.getLinha() - 1;
		int coluna = rainha.getColuna() + 1;
		
		while (linha >= 0 && coluna >= 0) {
			
			if(existeRainha(linha, coluna)){
				conflitos ++ ;
			}
			
			linha --;
			coluna ++;
			
		}
		
		linha = rainha.getLinha() + 1;
		coluna = rainha.getColuna()  - 1; 
		
//		para frente
		while (linha <= MAX_CASAS  && coluna <= MAX_CASAS) {
			
			if(existeRainha(linha, coluna)){
				conflitos ++ ;
			}
			
			linha ++;
			coluna --;
			
		}
		
		return conflitos;
		
	}
	
}
