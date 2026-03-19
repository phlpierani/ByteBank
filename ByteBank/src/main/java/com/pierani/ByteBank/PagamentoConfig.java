package com.pierani.ByteBank;

import jakarta.transaction.TransactionManager;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Configuration
public class PagamentoConfig {

    private final PlatformTransactionManager transactionManager;

    public PagamentoConfig(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job job(Step passoInicial, JobRepository jobRepository) {
        return new JobBuilder("PagamentoConfig", jobRepository)
                .start(passoInicial)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step passoInicial(ItemReader reader,
                             ItemWriter writer,
                             JobRepository jobRepository) {
        return new StepBuilder("passo-inicial", jobRepository)
                .<Pagamento, Pagamento>chunk(200)
                .reader(reader)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();

    }

    @Bean
    public ItemReader<Pagamento> reader() {
        return new FlatFileItemReaderBuilder<Pagamento>()
                .name("pagamento-csv")
                .resource(new FileSystemResource("file/dados_ficticios.csv"))
                .delimited()
                .names("nome", "cpf", "agencia", "conta", "valor", "mesReferencia")
                .targetType(Pagamento.class)
                .build();
    }

    @Bean
    public ItemWriter<Pagamento> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Pagamento>()
                .dataSource(dataSource)
                .sql(
                        "INSERT INTO pagamento (id, nome, cpf, agencia, conta, valor, mes_referencia) VALUES" +
                                "(:id, :nome, :cpf, :agencia, :conta, :valor, :mes_referencia, " + LocalDateTime.now() + ")"
                )
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }

}
