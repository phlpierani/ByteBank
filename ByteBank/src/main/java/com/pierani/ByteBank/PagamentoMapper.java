package com.pierani.ByteBank;

import org.springframework.batch.infrastructure.item.file.mapping.FieldSetMapper;
import org.springframework.batch.infrastructure.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class PagamentoMapper implements FieldSetMapper<Pagamento> {

    private DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("MM/yy");

    @Override
    public Pagamento mapFieldSet(FieldSet fieldSet) throws BindException {
        Pagamento pagamento = new Pagamento();
        pagamento.setNome(fieldSet.readString("nome"));
        pagamento.setCpf(fieldSet.readString("cpf"));
        pagamento.setAgencia(fieldSet.readInt("agencia"));
        pagamento.setConta(fieldSet.readString("conta"));
        pagamento.setValor(fieldSet.readDouble("valor"));
        pagamento.setMesReferencia(YearMonth.parse(fieldSet.readString("mesReferencia"), dataFormatter));
        return pagamento;
    }
}
