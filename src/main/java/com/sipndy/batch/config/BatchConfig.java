package com.sipndy.batch.config;

import com.sipndy.batch.repo.StudentRepository;
import com.sipndy.batch.student.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    //spring batch already provide us jobRepository resposible for storing all the meta data  of batch
    private final JobRepository jobRepository;
    //this is for tracking the transaction state of the data
    private final PlatformTransactionManager transactionManager;
    //User-Defined repository
    private final StudentRepository studentRepository;

    //Bean for reading the file
    @Bean
    public FlatFileItemReader<Student> itemReader(){
        var reader = new FlatFileItemReader<Student>();
        reader.setResource(new FileSystemResource("src/main/resources/students.csv"));
        reader.setName("csvReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        return reader;
    }

    //this one is resposbile for processing data as per business logic
    @Bean
    public StudentProcessor processor(){
        return new StudentProcessor();
    }

    //this bean is responsible for writing the data
    @Bean
    public RepositoryItemWriter<Student> write(){
        var writer = new RepositoryItemWriter<Student>();
        writer.setRepository(studentRepository);
        writer.setMethodName("save");
        return writer;
    }

    //Steps to read write and process the data
    @Bean
    public Step importStep(){
        return new StepBuilder("csvImport",jobRepository)
                .<Student,Student>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(write())
                .build();
    }
    //this is the job which will run the stesp
    @Bean
    public Job runJob(){
        return new JobBuilder("ImportStudents",jobRepository)
                .start(importStep())
                .build();
    }

    //Read the file line by line and add the value to the speciffied pojo
    private LineMapper<Student> lineMapper() {
        var lineMapper = new DefaultLineMapper<Student>();
        var lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("firstName","lastName","age");
        //help to transform the field read by reader into the specified pojo
        var fieldSetMapper = new BeanWrapperFieldSetMapper<Student>();
        fieldSetMapper.setTargetType(Student.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

}
